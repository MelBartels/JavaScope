import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;

/**
 * standalone class to update SiTech controller firmware so as to free UI;
 * includes status msg window;
 */
public class uploadFirmwareFile {
    int ix;
    int checksum;
    boolean blockAllxFFs;
    boolean responseReceived;
    int CRwaitCnt;
    String msg;

    boolean okToContinue = false;
    int blockCnt = 0;
    final int MAX_CR_WAIT_CNT = 15;
    final byte REC_BLOCK_OK = 0x0d;
    final byte REC_BLOCK_BAD = 0x0c;
    // block count must be < x7800/512(=60) (no data allowed past this point)
    final int MAX_UPLOAD_BLOCK = 0x7800/512;

    final int MILLISEC_WAIT_FOR_CONTROLLER_OK_TO_UPLOAD_FILE = 500;
    final int MILLISEC_WAIT_TO_BEGIN_UPLOAD = 125;
    final int MILLISEC_WAIT_FOR_CONTROLLER_RESPONSE_AFTER_BLOCK_XMT = 250;
    
    byte bytesIn[] = new byte[512];
    byte bytesOut[] = new byte[517];

    JFrameStatusMsg JFrameStatusMsg;
    File file;
    IO io;

    uploadFirmwareFile(IO io, File file) {
        this.io = io;
        this.file = file;

        JFrameStatusMsg = new JFrameStatusMsg();
        JFrameStatusMsg.show();
    }

    boolean upload() {
        try {
            displayStatusMsg("opening file...");
            FileInputStream in = new FileInputStream(file);

            // works for v1.1 and higher; otherwise ignored
            while (io.readSerialBuffer())
                ;
            io.writeStringPauseUntilXmtFinished(SITECH_ASCII_CMDS.uploadFirmwareFile.toString() + eString.RETURN);

            // wait for CR from controller
            CRwaitCnt = 0;
            msg = "waiting for controller OK to upload firmware file...";
            do {
                if (io.readSerialBuffer()) {
                    if (io.returnByteRead() == (byte) eString.RETURN)
                        okToContinue = true;
                }
                else {
                    msg += "..";
                    displayStatusMsg(msg);
                    common.threadSleep(MILLISEC_WAIT_FOR_CONTROLLER_OK_TO_UPLOAD_FILE);
                }
                CRwaitCnt++;
            } while (!okToContinue && CRwaitCnt < MAX_CR_WAIT_CNT);
            if (okToContinue) {
                displayStatusMsg("controller signals ready for firmware upload");
                // 10ms to 2 sec delay
                common.threadSleep(MILLISEC_WAIT_TO_BEGIN_UPLOAD);
            }
            else
                displayStatusMsg("timed out waiting for controller OK to upload firmware file...");

            // if OK to continue, and block count legal, and bytes in the upgrade file:
            // read in blocks and send them to controller as controller approves
            while (okToContinue && blockCnt < MAX_UPLOAD_BLOCK && in.read(bytesIn) != -1) {
                // grab 512 bytes and build checksum
                checksum = 0;
                blockAllxFFs = true;
                for (ix = 0; ix < 512; ix++) {
                    int i = (int) bytesIn[ix];
                    if (i < 0)
                        i+= 256;
                    if (i != 0xff)
                        blockAllxFFs = false;
                    checksum += i;
                }

                /**
                 * transmit erase block if block composed of all 0xFF's:
                 * For security reasons, the host should decode the recieved blocks to memory, and if the block
                 * is equal to all ff's, then the host should save that block as un-decoded ff's.  That way it
                 * won't have the repeated xored ff's which makes a pattern that someone could easily figure out.
                 */
                if (blockAllxFFs) {
                    // first output byte is the block number
                    bytesOut[0] = (byte) blockCnt;
                    bytesOut[1] = (byte) 0xFF;
                    bytesOut[2] = (byte) 0xFF;
                    console.stdOutLn("erasing block " + blockCnt);
                    // transmit these 3 bytes
                    io.writeByteArray(bytesOut, 3);
                }
                /**
                 * else transmit block as read from file
                 */
                else {
                    // first output byte is the block number
                    bytesOut[0] = (byte) blockCnt;
                    // output bytes #2, #3 indicate block size to be transmitted: 0*256 + 2*256 = 512 bytes to be transmitted
                    bytesOut[1] = 0;
                    bytesOut[2] = 2;

                    // checksum includes all bytes transmitted
                    for (ix = 0; ix < 3; ix++)
                        checksum += bytesOut[ix];

                    // fill out transmit buffer with the 512 bytes to send
                    for (ix = 0; ix < 512; ix++)
                        bytesOut[ix+3] = bytesIn[ix];
                    // fill out checksum in little endian order (low, then high byte)
                    bytesOut[515] = (byte) (checksum % 256);
                    bytesOut[516] = (byte) ((checksum %= 65536) / 256);

                    //for (ix = 0; ix < 517; ix++)
                    //    System.out.print(eString.intToHex(bytesOut[ix]) + "   ");
                    //console.getString();

                    // transmit out buffer
                    io.writeByteArray(bytesOut, 517);
                }

                // wait for response from controller then process response
                responseReceived = false;
                CRwaitCnt = 0;
                do {
                    if (io.readSerialBuffer()) {
                        responseReceived = true;
                        if (io.returnByteRead() == REC_BLOCK_OK) {
                            okToContinue = true;
                            displayStatusMsg("transmitted block "
                            + blockCnt
                            + " successfully");
                        }
                        else if (io.returnByteRead() == REC_BLOCK_BAD) {
                            okToContinue = false;
                            displayStatusMsg("something wrong with transmitted block, aborting...");
                            // controller will return to normal mode now...
                        }
                        else {
                            okToContinue = false;
                            displayStatusMsg("unknown controller response "
                            + eString.intToHex((int) io.returnByteRead())
                            + "]to block upload, aborting...");
                        }
                    }
                    else {
                        common.threadSleep(MILLISEC_WAIT_FOR_CONTROLLER_RESPONSE_AFTER_BLOCK_XMT);
                        CRwaitCnt++;
                    }
                } while (!responseReceived && CRwaitCnt < MAX_CR_WAIT_CNT);

                if (CRwaitCnt == MAX_CR_WAIT_CNT)
                    okToContinue = false;

                // increment block count and do it again
                blockCnt++;
            }
            // give result msg
            if (okToContinue && blockCnt == MAX_UPLOAD_BLOCK)
                displayStatusMsg("firmware updated successfully: cycle the power to the controller");
            else
                displayStatusMsg("firmware NOT updated successfully");

            in.close();
            return true;
        }
        catch (IOException ioe) {
            displayStatusMsg("could not open " + file.getName());
        }
        return false;
    }

    private void displayStatusMsg(final String msg) {
        console.stdOutLn(msg);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrameStatusMsg.updateMsg(msg);
            }
        });
    }
}
