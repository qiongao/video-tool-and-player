package GUI;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class VideoPlayer extends JPanel implements ActionListener {
    public static DrawPlayer videoLabel;
    public JFileChooser fc;
    public static ArrayList<File> rgbFiles = null;
    public static int frameIndex = 0;

    String playActionCommandStr = "Play";
    String pauseActionCommandStr = "Pause";
    String stopActionCommandStr = "Stop";
    String showBoxCommandStr = "Show Box";

    public static PlayVideoProcess playVideoProcess = null;
    public static PlaySoundProcess playSoundProcess = null;
//    DrawPlayer videoLabel;
    public static JLabel frameProcessLabel;

    public static FileInputStream soundInputStream = null;
    public static AudioInputStream audioInputStream = null;
    public static SourceDataLine audioDataLine = null;
    public static String soundFilePath = null;

    public static File videoDir;

    public static Map<Integer,ArrayList<ArrayList<Double>>> allError = null;
    public static Map<Integer,ArrayList<ArrayList<Integer>>> allLinks = null;
    public static Map<Integer,String> allSndVideoFiles = null;
    public static Map<Integer,Integer> allSndVideoFrames = null;
    public static Map<Integer,ArrayList<Integer>> allFirstVideoFrames = null;
    public static int[] oneFrameLink;
    public static int[] oneFrameError;

    public static boolean isVisible = false;
    public static boolean isShowBox = true;

    public VideoPlayer() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel videoControl = new JPanel();
        videoControl.setLayout(new BoxLayout(videoControl, BoxLayout.X_AXIS));
        videoLabel = new DrawPlayer();
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

        JButton showBoxButton = new JButton("Show Box");
        showBoxButton.setActionCommand(showBoxCommandStr);
        showBoxButton.addActionListener(this);

        buttonsBar.add(loadVideo);
        buttonsBar.addSeparator();
        buttonsBar.add(playButton);
        buttonsBar.add(pauseButton);
        buttonsBar.add(stopButton);
        buttonsBar.addSeparator();
        buttonsBar.add(showBoxButton);

        videoControl.add(videoLabel);
        videoControl.addMouseListener(videoLabel);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(playActionCommandStr)) {
            if (playVideoProcess == null || playVideoProcess.isDone()) {
                if (playVideoProcess != null) {
                    playVideoProcess.cancel(true);
                }

                playVideoProcess = new PlayVideoProcess(frameIndex, rgbFiles, videoLabel, frameProcessLabel);
                playVideoProcess.execute();
            }


            if (playSoundProcess == null) {
                playSoundProcess = new PlaySoundProcess();
                playSoundProcess.execute();
                audioDataLine.start();
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
            if (playVideoProcess != null) {
                playVideoProcess.setFrameIndex(0);
                playVideoProcess.cancel(true);
            }

            Utils.SetFrameShow(0, rgbFiles, videoLabel);
            VisibleRectangle(1);

            frameProcessLabel.setText("frame "+String.format("%04d", 1));

            if (playSoundProcess != null) {
                playSoundProcess.cancel(true);
            }

            if (audioDataLine != null) {
                audioDataLine.close();
            }
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
        } else if (e.getActionCommand().equals(showBoxCommandStr)) {
            isShowBox = !isShowBox;
            videoLabel.cleaner(videoLabel.getGraphics());
        }
    }

    public static void VisibleRectangle(int curFrame) {
        boolean flag = false;

        Set<Integer> v = allFirstVideoFrames.keySet();

        oneFrameLink = new int[v.size()];
        oneFrameError = new int[v.size()];

        Arrays.fill(oneFrameLink, -1);
        Arrays.fill(oneFrameError, -1);
        for(int z : v) {
            ArrayList<Integer> oneFrame = allFirstVideoFrames.get(z);
            for(int i = 0; i < oneFrame.size(); i++) {
                if((i + 1) < oneFrame.size()) {
                    int x = oneFrame.get(i);
                    int y = oneFrame.get(i+1);

                    if(curFrame == x || (curFrame > x && curFrame < y)) {
                        isVisible = true;
                        oneFrameLink[z] = i;
                        oneFrameError[z] = curFrame - x;
                        flag = true;
                        break;
                    }
                }
                else if((i + 1) >= oneFrame.size()) {
                    int x = oneFrame.get(i);
                    if(curFrame == x) {
                        isVisible = true;
                        oneFrameLink[z] = i;
                        flag = true;
                        break;
                    }
                }
            }
        }
        if(!flag) {
            videoLabel.cleaner(videoLabel.getGraphics());
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
                videoDir = fc.getSelectedFile();
                LoadVideo(videoDir);
                GetMetaInfo(videoDir);
                Utils.SetFrameShow(0, rgbFiles, videoLabel);
                VisibleRectangle(1);

                soundFilePath = getSoundFile(videoDir);

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
                } catch (FileNotFoundException | PlayWaveException | LineUnavailableException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void LoadVideo(File directory) {
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

    public static void GetMetaInfo(File directory) {
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
            int cnt = 0;
            allError = new HashMap<>();
            allLinks = new HashMap<>();
            allSndVideoFiles = new HashMap<>();
            allSndVideoFrames = new HashMap<>();
            allFirstVideoFrames = new HashMap<>();
            for (String line: metaContent) {
                String[] lineContent = line.split(" ");
                if (lineContent.length < 2) {
                    return;
                }

                String sndVideoName = lineContent[0];
                int sndVideoFrame = Integer.parseInt(lineContent[1]);
                allSndVideoFiles.put(cnt, sndVideoName);
                allSndVideoFrames.put(cnt, sndVideoFrame);

                ArrayList<ArrayList<Integer>> linkBook = new ArrayList<>();
                ArrayList<Integer> linkBookFrame = new ArrayList<>();
                for(int i=2; i+4<lineContent.length; i+=5) {
                    int firstVideoFrame = Integer.parseInt(lineContent[i]);
                    int x = Integer.parseInt(lineContent[i+1]);
                    int y = Integer.parseInt(lineContent[i+2]);
                    int width = Integer.parseInt(lineContent[i+3]);
                    int height = Integer.parseInt(lineContent[i+4]);

                    linkBook.add(new ArrayList<>(Arrays.asList(x, y, width, height)));
                    linkBookFrame.add(firstVideoFrame);
                }
                allLinks.put(cnt, linkBook);
                allFirstVideoFrames.put(cnt, linkBookFrame);
                ArrayList<ArrayList<Double>> tmpError = Utils.totalError(linkBook, linkBookFrame);
                allError.put(cnt, tmpError);
                cnt++;
            }
        }
    }

    public static String getSoundFile(File directory) {
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
}



class PlayVideoProcess extends SwingWorker<Void, Integer> {
    private int fIndex;
    ArrayList<File> rgbFiles;
    JLabel videoLabel;
    JLabel frameProcessLabel;
    public PlayVideoProcess(int frameIndex, ArrayList<File>  rgbFiles, JLabel videoLabel, JLabel frameProcessLabel) {
        this.fIndex = frameIndex;
        this.rgbFiles = rgbFiles;
        this.videoLabel = videoLabel;
        this.frameProcessLabel = frameProcessLabel;
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
                if (VideoPlayer.rgbFiles != null &&  VideoPlayer.rgbFiles.size() > fIndex) {
                    System.out.println("frame index: " + fIndex);
                    Utils.PlotRGBFile(VideoPlayer.rgbFiles.get(fIndex), VideoPlayer.videoLabel);
                    VideoPlayer.VisibleRectangle(fIndex+1);

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
    Boolean isPaused = false;

    public PlaySoundProcess() {}

    public void setPause() {
        isPaused = true;
        VideoPlayer.audioDataLine.stop();
//        aDataLine.stop();
    }

    public void disablePause() {
        if (isPaused) {
            isPaused = false;
            VideoPlayer.audioDataLine.drain();
            VideoPlayer.audioDataLine.start();
//            aDataLine.drain();
//            aDataLine.start();
        }
    }

    @Override
    protected Void doInBackground() {
        int readBytes = 0;
        byte[] audioBuffer = new byte[Constants.EXTERNAL_BUFFER_SIZE];

        try {
            while (readBytes != -1 && !isDone()) {
                if (!isPaused) {
                    readBytes = VideoPlayer.audioInputStream.read(audioBuffer, 0,
                            audioBuffer.length);
                    if (readBytes >= 0){
                        VideoPlayer.audioDataLine.write(audioBuffer, 0, readBytes);
                    }
                }
            }
        } catch (IOException e1) {
            VideoPlayer.audioDataLine.stop();
            VideoPlayer.audioDataLine.close();
        } finally {
            VideoPlayer.audioDataLine.stop();
            VideoPlayer.audioDataLine.close();
        }
        return null;
    }
}

class DrawPlayer extends JLabel implements MouseListener {

    public void paint(Graphics g) {
        super.paint(g);

        if(VideoPlayer.isVisible && VideoPlayer.isShowBox) {
            for(int i = 0; i< VideoPlayer.oneFrameLink.length;i++) {
                ArrayList<ArrayList<Integer>> oneframe = VideoPlayer.allLinks.get(i);
                ArrayList<ArrayList<Double>> oneError = VideoPlayer.allError.get(i);
                int x = VideoPlayer.oneFrameLink[i];
                if(x != -1) {
                    ArrayList<Integer> frame = oneframe.get(x);
                    int frameError = VideoPlayer.oneFrameError[i];
                    if(frameError == -1) {
                        ArrayList<Double> error = new ArrayList<>();
                        error.add(0.0);error.add(0.0);error.add(0.0);error.add(0.0);
                        hyperSuperPaint(g,frame,error,0);
                    }else {
                        ArrayList<Double> error = oneError.get(x);
                        hyperSuperPaint(g,frame,error,frameError);
                    }
                }
            }
        }
    }

    public void hyperSuperPaint(Graphics g, ArrayList<Integer> area, ArrayList<Double> error, int frameError) {
        int x = area.get(0) + (int)(error.get(0) * frameError);
        int y = area.get(1) + (int)(error.get(1) * frameError);
        int width = area.get(2) + (int)(error.get(2) * frameError);
        int height = area.get(3) + (int)(error.get(3) * frameError);

        g.setColor(Color.BLUE);
        g.drawRect(x, y, width, height);
    }

    public void cleaner(Graphics g) {
        super.paint(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX(), mouseY = e.getY();
        if(VideoPlayer.isVisible) {
            for(int i = 0; i< VideoPlayer.oneFrameLink.length;i++) {
                ArrayList<ArrayList<Integer>> oneframe = VideoPlayer.allLinks.get(i);
                ArrayList<ArrayList<Double>> oneError = VideoPlayer.allError.get(i);
                int x = VideoPlayer.oneFrameLink[i];
                if(x != -1) {
                    ArrayList<Integer> frame = oneframe.get(x);
                    int frameError = VideoPlayer.oneFrameError[i];
                    int rectX, rectY, rectWidth, rectHeight;
                    if(frameError == -1) {
                        ArrayList<Double> error = new ArrayList<>();
                        error.add(0.0);error.add(0.0);error.add(0.0);error.add(0.0);
                        rectX = frame.get(0) + (int)(error.get(0) * frameError);
                        rectY = frame.get(1) + (int)(error.get(1) * frameError);
                        rectWidth = frame.get(2) + (int)(error.get(2) * frameError);
                        rectHeight = frame.get(3) + (int)(error.get(3) * frameError);
                    }else {
                        ArrayList<Double> error = oneError.get(x);
                        rectX = frame.get(0) + (int)(error.get(0) * frameError);
                        rectY = frame.get(1) + (int)(error.get(1) * frameError);
                        rectWidth = frame.get(2) + (int)(error.get(2) * frameError);
                        rectHeight = frame.get(3) + (int)(error.get(3) * frameError);
                    }

                    if (mouseX - rectX <= rectWidth
                            && mouseX - rectX >= 0
                            && mouseY - rectY <= rectHeight
                            && mouseY - rectY >= 0
                    ) {
                        if (VideoPlayer.playVideoProcess != null) {
                            VideoPlayer.playVideoProcess.cancel(true);
                        }

                        if (VideoPlayer.playSoundProcess != null) {
                            VideoPlayer.playSoundProcess.cancel(true);
                        }

                        if (VideoPlayer.audioDataLine != null) {
                            VideoPlayer.audioDataLine.close();
                        }

                        VideoPlayer.playVideoProcess = null;
                        VideoPlayer.playSoundProcess = null;

                        String sndVideoName = VideoPlayer.allSndVideoFiles.get(i);
                        int sndVideoFrame = VideoPlayer.allSndVideoFrames.get(i);
                        VideoPlayer.frameIndex = sndVideoFrame - 1;

                        File videoDir = Paths.get(VideoPlayer.videoDir.getParent()).resolve(sndVideoName).toFile();
                        VideoPlayer.LoadVideo(videoDir);
                        VideoPlayer.GetMetaInfo(videoDir);
                        Utils.SetFrameShow(sndVideoFrame, VideoPlayer.rgbFiles, VideoPlayer.videoLabel);
                        VideoPlayer.VisibleRectangle(sndVideoFrame);

                        VideoPlayer.soundFilePath = VideoPlayer.getSoundFile(videoDir);

                        try {
                            VideoPlayer.soundInputStream = new FileInputStream(VideoPlayer.soundFilePath);
                            try {
                                VideoPlayer.audioInputStream.close();
                                VideoPlayer.audioInputStream = null;

                                InputStream bufferedIn = new BufferedInputStream(VideoPlayer.soundInputStream); // new
                                VideoPlayer.audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                                AudioFormat format = VideoPlayer.audioInputStream.getFormat();
                                long frameSize = format.getFrameSize();
                                float sndVideoSecond = (float)sndVideoFrame / 30.0f;
                                float skipSize = (float)format.getFrameSize() * format.getFrameRate() * sndVideoSecond;
                                skipSize = (long) (skipSize / frameSize) * frameSize;

                                VideoPlayer.audioInputStream.skip((long) skipSize);
                            } catch (UnsupportedAudioFileException | IOException e1) {
                                throw new PlayWaveException(e1);
                            }

                            // Obtain the information about the AudioInputStream
                            AudioFormat audioFormat = VideoPlayer.audioInputStream.getFormat();
                            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

                            VideoPlayer.audioDataLine = (SourceDataLine) AudioSystem.getLine(info);
                            VideoPlayer.audioDataLine.open(audioFormat, Constants.EXTERNAL_BUFFER_SIZE);
                        } catch (FileNotFoundException | PlayWaveException | LineUnavailableException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}