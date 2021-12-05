package GUI;

import javafx.print.Collation;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AuthoringTool extends JPanel {
    public AuthoringTool() throws BadLocationException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        VideoPart videoPart = new VideoPart();
        JComponent controlPart = new ControlPart(videoPart);

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(controlPart);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(videoPart);

    }
    public static void main(String[] args) throws BadLocationException {
        JFrame frame = new JFrame();
        frame.setContentPane(new AuthoringTool());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}

class ControlPart extends JPanel {
    public ControlPart(VideoPart videoPart) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        ActionPart ActionChoiceList = new ActionPart(videoPart);
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
    public FirstVideo VideoPart1;
    public SecondVideo VideoPart2;
    public VideoPart() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        VideoPart1 = new FirstVideo();
        VideoPart2 = new SecondVideo();

        add(VideoPart1);
        add(VideoPart2);
    }
}

class FirstVideo extends JPanel implements ChangeListener {
    ArrayList<File> rgbFiles = null;
    JLabel frameLabel;
    JLabel videoPart;
    public FirstVideo() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        videoPart = new JLabel();
        videoPart.setMaximumSize(new Dimension(352, 288));
        videoPart.setPreferredSize(new Dimension(352, 288));
        videoPart.setBorder(BorderFactory.createTitledBorder(""));
        videoPart.setAlignmentX(CENTER_ALIGNMENT);

        JSlider videoProgressSlider = new JSlider(JSlider.HORIZONTAL, 1, 9000, 1) {
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this));
            }
        };
        videoProgressSlider.setMaximumSize(new Dimension(360, 30));
        videoProgressSlider.setPreferredSize(new Dimension(360, 30));
        videoProgressSlider.setMajorTickSpacing(1000);
        videoProgressSlider.setMinorTickSpacing(100);
        videoProgressSlider.setPaintTicks(true);
        videoProgressSlider.addChangeListener(this);

        frameLabel = new JLabel("Frame 0001");
        frameLabel.setAlignmentX(CENTER_ALIGNMENT);

        add(videoPart);
        add(videoProgressSlider);
        add(frameLabel);
    }

    public void LoadVideo(File directory) {
        File[] listOfFiles = directory.listFiles();
        rgbFiles = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                String filename = file.getName();
                String fileType = "";
                int lastIndex = filename.lastIndexOf('.');
                if (lastIndex == -1) continue;
                else {
                    fileType = filename.substring(lastIndex + 1);
                }
                if (fileType.equals("rgb")) {
                    rgbFiles.add(file);
                }
            }

            rgbFiles.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        int progress = source.getValue();
        frameLabel.setText("frame "+String.format("%04d", progress));

        if (rgbFiles != null && rgbFiles.size() >= progress) {
            plotRGBFile(rgbFiles.get(progress-1));
        }
    }

    public void SetFirstFrame() {
        if (rgbFiles != null && rgbFiles.size() > 0) {
            plotRGBFile(rgbFiles.get(0));
        }
    }

    private void plotRGBFile(File rgbFile) {
        BufferedImage img = ImageDisplay.GetImage(rgbFile);
        if (img != null) {
            videoPart.setIcon(new ImageIcon(img));
        }
    }
}

class SecondVideo extends JPanel implements ChangeListener {
    ArrayList<File> rgbFiles = null;
    JLabel frameLabel;
    JLabel videoPart;
    public SecondVideo() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        videoPart = new JLabel();
        videoPart.setMaximumSize(new Dimension(352, 288));
        videoPart.setPreferredSize(new Dimension(352, 288));
        videoPart.setBorder(BorderFactory.createTitledBorder(""));
        videoPart.setAlignmentX(CENTER_ALIGNMENT);


        JSlider videoProgressSlider = new JSlider(JSlider.HORIZONTAL, 1, 9000, 1) {
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this));
            }
        };
        videoProgressSlider.setMaximumSize(new Dimension(360, 30));
        videoProgressSlider.setPreferredSize(new Dimension(360, 30));
        videoProgressSlider.setMajorTickSpacing(1000);
        videoProgressSlider.setMinorTickSpacing(100);
        videoProgressSlider.setPaintTicks(true);
        videoProgressSlider.addChangeListener(this);

        frameLabel = new JLabel("Frame 0001");
        frameLabel.setAlignmentX(CENTER_ALIGNMENT);

        add(videoPart);
        add(videoProgressSlider);
        add(frameLabel);
    }

    public void LoadVideo(File directory) {
        File[] listOfFiles = directory.listFiles();
        rgbFiles = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                String filename = file.getName();
                String fileType = "";
                int lastIndex = filename.lastIndexOf('.');
                if (lastIndex == -1) continue;
                else {
                    fileType = filename.substring(lastIndex + 1);
                }
                if (fileType.equals("rgb")) {
                    rgbFiles.add(file);
                }
            }

            rgbFiles.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        int progress = source.getValue();
        frameLabel.setText("frame "+String.format("%04d", progress));

        if (rgbFiles != null && rgbFiles.size() >= progress) {
            plotRGBFile(rgbFiles.get(progress-1));
        }
    }

    public void SetFirstFrame() {
        if (rgbFiles != null && rgbFiles.size() > 0) {
            plotRGBFile(rgbFiles.get(0));
        }
    }

    private void plotRGBFile(File rgbFile) {
        BufferedImage img = ImageDisplay.GetImage(rgbFile);
        if (img != null) {
            videoPart.setIcon(new ImageIcon(img));
        }
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
    JLabel directoryLabel;

    VideoPart videoPart;

    public ActionPart(VideoPart videoPart) {
        this.videoPart = videoPart;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        listModel = new DefaultListModel();
        listModel.addElement("Import Primary Video");
        listModel.addElement("Import Secondary Video");
        listModel.addElement("Create New Hyperlink");

        //Create the list and put it in a scroll pane.
        actionList = new JList(listModel);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));

        JLabel actionLabel = new JLabel("Action: ");
        actionLabel.setLabelFor(actionList);
//        actionLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

        directoryLabel = new JLabel("no path is selected");

        labelPanel.add(actionLabel);
        labelPanel.add(directoryLabel);


        actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionList.setSelectedIndex(0);
        actionList.addListSelectionListener(this);
        actionList.setMaximumSize(new Dimension(200, 90));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        openDirButton = new JButton("Open a Directory");
        openDirButton.addActionListener(new OpenFileListener());
        openDirButton.setEnabled(true);

        submitButton = new JButton(submitString);
        ActionSubmitListener submitListener = new ActionSubmitListener();
        submitButton.setActionCommand(submitString);
        submitButton.addActionListener(submitListener);
        submitButton.setEnabled(false);

        buttonPanel.add(openDirButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(submitButton);

        add(labelPanel);
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
                    directoryLabel.setText(String.valueOf(PrimaryVideoDir));
                    videoPart.VideoPart1.LoadVideo(PrimaryVideoDir);
                    videoPart.VideoPart1.SetFirstFrame();
                } else if (index == 1) {
                    SecondaryVideoDir = fc.getSelectedFile();
                    System.out.println("Import Secondary Video Directory: " + SecondaryVideoDir);
                    directoryLabel.setText(String.valueOf(SecondaryVideoDir));
                    videoPart.VideoPart2.LoadVideo(SecondaryVideoDir);
                    videoPart.VideoPart2.SetFirstFrame();
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

    private static final String reNameString = "Set Name";
    private JButton setNameButton;
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

        setNameButton = new JButton(reNameString);
//        reNameButton.setActionCommand(reNameString);
        RenameListener renameListener = new RenameListener();
        setNameButton.addActionListener(renameListener);
        setNameButton.setEnabled(false);

        newName = new JTextField(10);
        newName.addActionListener(renameListener);
        newName.getDocument().addDocumentListener(renameListener);
        newName.setMaximumSize(new Dimension(150, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(newName);
        buttonPanel.add(setNameButton);

        add(selectLinkLabel);
        add(listScrollPane, BorderLayout.CENTER);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(buttonPanel);
    }

    class RenameListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = newName.getText();

            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                newName.requestFocusInWindow();
                newName.selectAll();
                return;
            }

            int index = linkList.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                disableButton();
                return;
            }

            listModel.setElementAt(newName.getText(), index);
//            listModel.insertElementAt(newName.getText(), index);
            //If we just wanted to add to the end, we'd do this:
            //listModel.addElement(employeeName.getText());

            //Reset the text field.
            newName.requestFocusInWindow();
            newName.setText("");

            //Select the new item and make it visible.
            linkList.setSelectedIndex(index);
            linkList.ensureIndexIsVisible(index);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                setNameButton.setEnabled(true);
            }
        }

        private void disableButton() {
            setNameButton.setEnabled(false);
            alreadyEnabled = false;
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                disableButton();
                return true;
            }
            return false;
        }

        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        setNameButton.setEnabled(linkList.getSelectedIndex() != -1);
    }
}