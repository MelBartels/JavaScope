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
 *displays a JList box of coordinate positions to select from;
 *upon selecting a single coordinate, a label per updateLabelMediator is updated and the frame is closed;
 *selectedPosition is set to the choosen coordinate position;
 */
public class JListSelectPosition implements IJListSelectPosition {
    private JFrame frame;
    private JPanel panel;
    private JList list;
    protected DefaultListModel model;
    private JScrollPane pane;
    protected listPosition listPos;
    private updateLabelMediator updateLabelMediator;
    position selectedPosition;

    JListSelectPosition(listPosition listPos, updateLabelMediator updateLabelMediator) {
        this.listPos = listPos;
        this.updateLabelMediator = updateLabelMediator;
        model = new DefaultListModel();
        createList();
        list = new JList(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new selectionListener());
        frame = new JFrame("1 click select");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = (JPanel) frame.getContentPane();
        pane = new JScrollPane(list);
        panel.add(pane, BorderLayout.CENTER);
        frame.pack();
        screenPlacement.getInstance().center(frame);
        frame.setVisible(true);
    }

    void createList() {
        Iterator it;
        position p;

        it = listPos.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            model.addElement(p.buildStringDataFileFormat());
        }
    }

    public position selectedPosition() {
        return selectedPosition;
    }

    class selectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                selectedPosition = listPos.get(list.getSelectedIndex());
                updateLabelMediator.update(selectedPosition.buildStringDataFileFormat());
                frame.setVisible(false);
            }
        }
    }
}

