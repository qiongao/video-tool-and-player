package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoPlayer extends JPanel implements ActionListener {
    public JFileChooser fc;
    ArrayList<File> rgbFiles = null;
    public int frameIndex = 0;
    Boolean isPlay = false;
    String playActionCommandStr = "Play";
    String pauseActionCommandStr = "Pause";
    String stopActionCommandStr = "Stop";

    PlayVideoProcess playVideoProcess;
    PlaySoundProcess playSoundProcess;
    JLabel videoLabel;
    JLabel frameProcessLabel;
    PlaySound soundPlayer;

    public VideoPlayer() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel videoControl = new JPanel();
        videoControl.setLayout(new BoxLayout(videoControl, BoxLayout.X_AXIS));
        videoLabel = new JLabel();
        videoLabel.setMaximumSize(new Dimension(352, 288));
        videoLabel.setPreferredSize(new Dimension(352, 288));
        videoLabel.setBorder(BorderFactory.createTitledBorder(""));
        videoLabel.setAlignmentX(CENTER_ALIGNMENT);

        frameProcessLabel = new JLabel("Playing Frame 0001");
        frameProcessLabel.setAlignmentX(CENTER_ALIGNMENT);

        JToolBar buttonsBar = new JToolBar(JToolBar.VERTICAL);
        buttonsBar.setFloatable(false);
        buttonsBar.setRollover(true);

        JButton loadVideo = new JButton("Load");
        loadVideo.addActionListener(new OpenFileListener(videoLabel));
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        playVideoProcess = new PlayVideoProcess();
        playSoundProcess = new PlaySoundProcess();

        JButton playButton = new JButton("Play");
        playButton.setActionCommand(playActionCommandStr);
        playButton.addActionListener(this);
//        playButton.addActionListener(new playVideoListener(videoLabel, frameProcessLabel));
        JButton pauseButton = new JButton("Pause");
        pauseButton.setActionCommand(pauseActionCommandStr);
        pauseButton.addActionListener(this);
//        pauseButton.addActionListener(new pauseVideoListener());
        JButton stopButton = new JButton("Stop");
        stopButton.setActionCommand(stopActionCommandStr);
        stopButton.addActionListener(this);
//        stopButton.addActionListener(new stopVideoListener(frameProcessLabel));

        buttonsBar.add(loadVideo);
        buttonsBar.addSeparator();
        buttonsBar.add(playButton);
        buttonsBar.add(pauseButton);
        buttonsBar.add(stopButton);

        videoControl.add(videoLabel);
        videoControl.add(buttonsBar);

        add(videoControl);
        add(frameProcessLabel);
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setContentPane(new VideoPlayer());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private void plotRGBFile(File rgbFile) {
        BufferedImage img = ImageDisplay.GetImage(rgbFile);
        if (img != null) {
            videoLabel.setIcon(new ImageIcon(img));
        }
    }

    public void SetFirstFrame() {
        if (rgbFiles != null && rgbFiles.size() > 0) {
            plotRGBFile(rgbFiles.get(0));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(playActionCommandStr)) {
            playVideoProcess.execute();
            playSoundProcess.execute();
        } else if (actionCommand.equals(pauseActionCommandStr)) {
            isPlay = false;
        } else if (actionCommand.equals(stopActionCommandStr)) {
            isPlay = false;
            frameIndex = 0;
            frameProcessLabel.setText("frame "+String.format("%04d", 0));
        }
    }

    class OpenFileListener extends Component implements ActionListener {
        JLabel videoLabel;
        public OpenFileListener(JLabel videoLabel) {
            this.videoLabel = videoLabel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File videoDir = fc.getSelectedFile();
                LoadVideo(videoDir);
                SetFirstFrame();
                String soundFilePath = getSoundFile(videoDir);
                FileInputStream soundInputStream;
                try {
                    soundInputStream = new FileInputStream(soundFilePath);
                    soundPlayer = new PlaySound(soundInputStream);
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        }

        public String getSoundFile(File directory) {
            File[] listOfFiles = directory.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    String filename = file.getName();
                    String fileType = "";
                    int lastIndex = filename.lastIndexOf('.');
                    if (lastIndex == -1) continue;
                    else {
                        fileType = filename.substring(lastIndex + 1);
                    }
                    if (fileType.equals("wav")) {
                        return file.getPath();
                    }
                }
            }
            return "";
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
    }

    class PlayVideoProcess extends SwingWorker<Void, Integer> {

        @Override
        protected Void doInBackground() {
            isPlay = true;

            while(isPlay) {
                if (rgbFiles != null &&  rgbFiles.size() > frameIndex) {
                    plotRGBFile(rgbFiles.get(frameIndex));
                    frameProcessLabel.setText("frame "+String.format("%04d", frameIndex+1));

                    frameIndex += 1;
                    try {
//                    TimeUnit.SECONDS.sleep(300/9000);
                        long timeGap = 300000/9;
                        TimeUnit.MICROSECONDS.sleep(timeGap);
                    } catch (InterruptedException ignored) {}
                }
            }
            return null;
        }
    }

    class PlaySoundProcess extends SwingWorker<Void, Integer> {
        @Override
        protected Void doInBackground() {
            isPlay = true;
            try {
                soundPlayer.play();
            } catch (PlayWaveException exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }
}
