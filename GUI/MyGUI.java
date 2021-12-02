package GUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;

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
    public ControlPart() throws BadLocationException {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//        JLayeredPane controlPart = new JLayeredPane();
//        controlPart.setPreferredSize(new Dimension(600, 150));
//        controlPart.setBorder(BorderFactory.createTitledBorder(""));
//
//        add(controlPart);

        JTextPane actionTextPane = new JTextPane();
        StyledDocument actionDoc = actionTextPane.getStyledDocument();
        actionDoc.insertString(actionDoc.getLength(), "Import Primary video", actionDoc.getStyle("regular"));
        actionTextPane.setPreferredSize(new Dimension(100, 100));

        JLabel actionLabel = new JLabel("Action: ");
        actionLabel.setLabelFor(actionTextPane);

        JTextPane selectLinkTextPane = new JTextPane();
        StyledDocument selectLinkDoc = selectLinkTextPane.getStyledDocument();
        selectLinkDoc.insertString(selectLinkDoc.getLength(), "Doctor", selectLinkDoc.getStyle("regular"));
        selectLinkTextPane.setPreferredSize(new Dimension(100, 100));

        JLabel selectLinkLabel = new JLabel("Select Link: ");
        selectLinkLabel.setLabelFor(actionTextPane);

        JButton ConnectVideoButton = new JButton("Connect Video");
        ConnectVideoButton.setVerticalTextPosition(AbstractButton.CENTER);
        JButton SaveFileButton = new JButton("Save File");
        SaveFileButton.setVerticalTextPosition(AbstractButton.CENTER);

        add(actionLabel);
        add(actionTextPane);

        add(Box.createRigidArea(new Dimension(10, 0)));

        add(selectLinkLabel);
        add(selectLinkTextPane);

        add(Box.createRigidArea(new Dimension(10, 0)));
        add(ConnectVideoButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(SaveFileButton);
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
