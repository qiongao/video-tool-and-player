package GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

public class MyGUI extends JPanel {
    public MyGUI() throws BadLocationException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JComponent controlPart = new ControlPart();
        JComponent videoPart = new VideoPart();

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(controlPart);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(videoPart);

    }
    public static void main(String[] args) throws BadLocationException {
        JFrame frame = new JFrame();
        frame.setContentPane(new MyGUI());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}

class ControlPart extends JPanel {
    public ControlPart() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        ActionPart ActionChoiceList = new ActionPart();
        LinkListPart selectLinkList = new LinkListPart();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        JButton ConnectVideoButton = new JButton("Connect Video");
        ConnectVideoButton.setVerticalTextPosition(AbstractButton.CENTER);
        JButton SaveFileButton = new JButton("Save File");
        SaveFileButton.setVerticalTextPosition(AbstractButton.CENTER);
        buttonsPanel.add(ConnectVideoButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(SaveFileButton);

        add(ActionChoiceList);
        add(Box.createRigidArea(new Dimension(50, 0)));
        add(selectLinkList);
        add(Box.createRigidArea(new Dimension(50, 0)));
        add(buttonsPanel);
    }
}

class VideoPart extends JPanel  {
    public VideoPart() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JComponent videoPart1 = new FirstVideo();
        JComponent videoPart2 = new SecondVideo();

        add(videoPart1);
        add(videoPart2);
    }
}

class FirstVideo extends JPanel {
    public FirstVideo() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLayeredPane videoPart = new JLayeredPane();
        videoPart.setPreferredSize(new Dimension(352, 288));
        videoPart.setBorder(BorderFactory.createTitledBorder("Video1"));

        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                0, 10, 5);

        add(videoPart);
        add(framesPerSecond);
    }
}

class SecondVideo extends JPanel {
    public SecondVideo() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLayeredPane videoPart = new JLayeredPane();
        videoPart.setPreferredSize(new Dimension(352, 288));
        videoPart.setBorder(BorderFactory.createTitledBorder("Video2"));

        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                0, 10, 5);

        add(videoPart);
        add(framesPerSecond);
    }
}

class ActionPart extends JPanel implements ListSelectionListener {
    public JList actionList;
    public DefaultListModel listModel;

    public static final String submitString = "Submit";
    public JButton submitButton;
    public JButton openDirButton;

    public File PrimaryVideoDir = null, SecondaryVideoDir = null;

    public JFileChooser fc;

    public ActionPart() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        listModel = new DefaultListModel();
        listModel.addElement("Import Primary Video");
        listModel.addElement("Import Secondary Video");
        listModel.addElement("Create New Hyperlink");

        //Create the list and put it in a scroll pane.
        actionList = new JList(listModel);

        JLabel actionLabel = new JLabel("Action: ");
        actionLabel.setLabelFor(actionList);
        actionLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

        actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionList.setSelectedIndex(0);
        actionList.addListSelectionListener(this);
//        actionList.setMaximumSize(new Dimension(200, 0));
//        actionList.setPreferredSize(new Dimension(300, 100));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        openDirButton = new JButton("Open a Directory");
        openDirButton.addActionListener(new OpenFileListener());
        openDirButton.setEnabled(true);

        submitButton = new JButton(submitString);
        ActionSubmitListener hireListener = new ActionSubmitListener();
        submitButton.setActionCommand(submitString);
        submitButton.addActionListener(hireListener);
        submitButton.setEnabled(false);

        buttonPanel.add(openDirButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(submitButton);

        add(actionLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(actionList);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(buttonPanel);
    }

    class OpenFileListener extends Component implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                int index = actionList.getSelectedIndex();
                if (index == 0) {
                    PrimaryVideoDir = fc.getSelectedFile();
                    System.out.println("Import Primary Video Directory: " + PrimaryVideoDir);
                } else if (index == 1) {
                    SecondaryVideoDir = fc.getSelectedFile();
                    System.out.println("Import Secondary Video Directory: " + PrimaryVideoDir);
                }
            }
        }
    }

    class ActionSubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = actionList.getSelectedIndex();


        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // whether import directory
            int index = actionList.getSelectedIndex();
            openDirButton.setEnabled(index != 2);

            if (index == 0) {
                submitButton.setEnabled(PrimaryVideoDir != null);
            } else if (index == 1) {
                submitButton.setEnabled(SecondaryVideoDir != null);
            } else {
                submitButton.setEnabled(true);
            }
        }
    }
}

class LinkListPart extends JPanel implements ListSelectionListener {
    private JList linkList;
    private DefaultListModel listModel;

    private static final String reNameString = "Update Name";
    private JButton reNameButton;
    private JTextField newName;

    public LinkListPart () {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        listModel = new DefaultListModel();
        listModel.addElement("Doctor");
        listModel.addElement("Dinosaur");
        listModel.addElement("Dinosaur2");

        linkList = new JList(listModel);
        JLabel selectLinkLabel = new JLabel("Select Link: ");
        selectLinkLabel.setLabelFor(linkList);
        selectLinkLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

        linkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linkList.setSelectedIndex(0);
        linkList.addListSelectionListener(this);
        linkList.setVisibleRowCount(4);
        JScrollPane listScrollPane = new JScrollPane(linkList);
        listScrollPane.setMaximumSize(new Dimension(200, 90));

        reNameButton = new JButton(reNameString);
//        reNameButton.setActionCommand(reNameString);
        RenameListener renameListener = new RenameListener();
        reNameButton.addActionListener(renameListener);
        reNameButton.setEnabled(false);

        newName = new JTextField(10);
        newName.addActionListener(renameListener);
        newName.getDocument().addDocumentListener(renameListener);
        String name = listModel.getElementAt(
                linkList.getSelectedIndex()).toString();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(newName);
        buttonPanel.add(reNameButton);
//        listScrollPane.setPreferredSize(new Dimension(100, 100));
//        linkList.setMaximumSize(new Dimension(200, 0));
//        actionList.setPreferredSize(new Dimension(300, 100));

        add(selectLinkLabel);
//        add(linkList);
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPanel);
    }

    class RenameListener implements ActionListener, DocumentListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }

        @Override
        public void insertUpdate(DocumentEvent e) {

        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }
}