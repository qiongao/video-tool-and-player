package GUI;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class VideoPlayer extends JPanel implements ActionListener {
    public JFileChooser fc;
    ArrayList<File> rgbFiles = null;
    public int frameIndex = 0;

    String playActionCommandStr = "Play";
    String pauseActionCommandStr = "Pause";
    String stopActionCommandStr = "Stop";

    PlayVideoProcess playVideoProcess = null;
    PlaySoundProcess playSoundProcess = null;
    JLabel videoLabel;
    JLabel frameProcessLabel;

    FileInputStream soundInputStream = null;
    AudioInputStream audioInputStream = null;
    SourceDataLine audioDataLine = null;
    String soundFilePath = null;

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

        JButton playButton = new JButton("Play");
        playButton.setActionCommand(playActionCommandStr);
        playButton.addActionListener(this);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setActionCommand(pauseActionCommandStr);
        pauseButton.addActionListener(this);

        JButton stopButton = new JButton("Stop");
        stopButton.setActionCommand(stopActionCommandStr);
        stopButton.addActionListener(this);

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
            if (playVideoProcess == null || playVideoProcess.isDone()) {
                if (playVideoProcess != null) {
                    playVideoProcess.cancel(true);
                }

                playVideoProcess = new PlayVideoProcess(frameIndex);
                playVideoProcess.execute();
            }


            if (playSoundProcess == null) {
                audioDataLine.start();
                playSoundProcess = new PlaySoundProcess(audioDataLine, audioInputStream);
                playSoundProcess.execute();
            } else {
                playSoundProcess.disablePause();
            }
        } else if (actionCommand.equals(pauseActionCommandStr)) {
            if (playVideoProcess == null || playSoundProcess == null) {
                return;
            }

            playVideoProcess.cancel(true);
            playVideoProcess.run();
            frameIndex = playVideoProcess.getFrameIndex();

            playSoundProcess.setPause();
        } else if (actionCommand.equals(stopActionCommandStr)) {
            frameIndex = 0;
            playVideoProcess.setFrameIndex(0);
            playVideoProcess.cancel(true);

            SetFirstFrame();
            frameProcessLabel.setText("frame "+String.format("%04d", 0));

            playSoundProcess.cancel(true);
            playSoundProcess = null;

            if (soundFilePath == null) {
                return;
            }
            try {
                soundInputStream = new FileInputStream(soundFilePath);
                audioInputStream = null;
                try {
                    InputStream bufferedIn = new BufferedInputStream(soundInputStream); // new
                    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                } catch (UnsupportedAudioFileException | IOException e1) {
                    throw new PlayWaveException(e1);
                }

                // Obtain the information about the AudioInputStream
                AudioFormat audioFormat = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

                audioDataLine = (SourceDataLine) AudioSystem.getLine(info);
                audioDataLine.open(audioFormat, Constants.EXTERNAL_BUFFER_SIZE);
            } catch (PlayWaveException | LineUnavailableException | FileNotFoundException exception) {
                exception.printStackTrace();
            }
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
                soundFilePath = getSoundFile(videoDir);

                try {
                    soundInputStream = new FileInputStream(soundFilePath);
//                    soundPlayer = new PlaySound(soundInputStream);

                    audioInputStream = null;
                    try {
                        InputStream bufferedIn = new BufferedInputStream(soundInputStream); // new
                        audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                    } catch (UnsupportedAudioFileException | IOException e1) {
                        throw new PlayWaveException(e1);
                    }

                    // Obtain the information about the AudioInputStream
                    AudioFormat audioFormat = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

                    audioDataLine = (SourceDataLine) AudioSystem.getLine(info);
                    audioDataLine.open(audioFormat, Constants.EXTERNAL_BUFFER_SIZE);
                } catch (FileNotFoundException | PlayWaveException | LineUnavailableException exception) {
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

        public void getMetaInfo(File directory) {
            File[] listOfFiles = directory.listFiles();
            List<String> metaContent = null;
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    String filename = file.getName();
                    if (filename.equals("meta.txt")) {
                        try {
                            metaContent = Files.readAllLines(Paths.get(file.getPath()));
                        } catch (Exception ignored) {}
                        break;
                    }
                }
            }

            if (metaContent != null) {
                for (String line: metaContent) {
                    String[] lineContent = line.split(" ");
                    // TODO
                }
            }
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

                rgbFiles.sort(Comparator.comparing(File::getName));
            }
        }
    }

    class PlayVideoProcess extends SwingWorker<Void, Integer> {
        private int fIndex;
        public PlayVideoProcess(int frameIndex) {
            this.fIndex = frameIndex;
        }

        @Override
        protected Void doInBackground() {
            long startTime = System.currentTimeMillis();
            int timeIndex = 0;
            Boolean[] playedFrames = new Boolean[Constants.FramesPerSecond];
            Arrays.fill(playedFrames, false);

            while (!isDone()) {
                long curTime = System.currentTimeMillis();
                if (timeIndex < Constants.FramesPerSecond && !playedFrames[timeIndex] && curTime >= startTime + timeIndex * 33) {
                    playedFrames[timeIndex++] = true;
                    if (rgbFiles != null &&  rgbFiles.size() > fIndex) {
                        System.out.println("frame index: " + fIndex);
                        plotRGBFile(rgbFiles.get(fIndex));
                        frameProcessLabel.setText("frame "+String.format("%04d", fIndex+1));

                        fIndex += 1;
                    }
                } else if (timeIndex >= Constants.FramesPerSecond && curTime >= startTime + 1000) {
                    timeIndex = 0;
                    Arrays.fill(playedFrames, false);
                    startTime += 1000;
                }
            }
            return null;
        }

        int getFrameIndex() {
            return this.fIndex;
        }

        void setFrameIndex(int frameIndex) {
            this.fIndex = frameIndex;
        }
    }

    class PlaySoundProcess extends SwingWorker<Void, Integer> {
        SourceDataLine aDataLine;
        AudioInputStream aInputStream;
        Boolean isPaused = false;

        public PlaySoundProcess(SourceDataLine audioDataLine, AudioInputStream audioInputStream) {
            this.aDataLine = audioDataLine;
            this.aInputStream = audioInputStream;
        }

        public AudioInputStream getAudioInputStream() {
            return this.aInputStream;
        }

        public void setPause() {
            isPaused = true;
            aDataLine.stop();
        }

        public void disablePause() {
            if (isPaused) {
                isPaused = false;
                aDataLine.drain();
                aDataLine.start();
            }
        }

        @Override
        protected Void doInBackground() {
            int readBytes = 0;
            byte[] audioBuffer = new byte[Constants.EXTERNAL_BUFFER_SIZE];

            try {
                while (readBytes != -1 && !isDone()) {
                    if (!isPaused) {
                        readBytes = this.aInputStream.read(audioBuffer, 0,
                                audioBuffer.length);
                        if (readBytes >= 0){
                            aDataLine.write(audioBuffer, 0, readBytes);
                        }
                    }
                }
            } catch (IOException e1) {
                aDataLine.stop();
                aDataLine.close();
            } finally {
                // plays what's left and and closes the audioChannel
//                aDataLine.drain();
                aDataLine.stop();
                aDataLine.close();
            }
            return null;
        }
    }
}
