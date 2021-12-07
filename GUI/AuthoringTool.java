package GUI;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

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
	
	public static JButton ConnectVideoButton;
	public static JButton SaveFileButton;
	
	
    public ControlPart(VideoPart videoPart) {
    	
    	
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        ActionPart ActionChoiceList = new ActionPart(videoPart);
        LinkListPart selectLinkList = new LinkListPart();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        ConnectVideoButton = new JButton("Connect Video");
        ConnectVideoButton.setVerticalTextPosition(AbstractButton.CENTER);
        ActionConnectAndSaveListener submitListener = new ActionConnectAndSaveListener();
        
        ConnectVideoButton.addActionListener(submitListener);
        ConnectVideoButton.setEnabled(false);
        
        
        SaveFileButton = new JButton("Save File");
        SaveFileButton.setVerticalTextPosition(AbstractButton.CENTER);
        SaveFileButton.addActionListener(submitListener);
        SaveFileButton.setEnabled(false);
        buttonsPanel.add(ConnectVideoButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(SaveFileButton);

        add(ActionChoiceList);
        add(Box.createRigidArea(new Dimension(50, 0)));
        add(selectLinkList);
        add(Box.createRigidArea(new Dimension(50, 0)));
        add(buttonsPanel);
    }
    
    
    class ActionConnectAndSaveListener implements ActionListener {
    	int x = 0;
        public void actionPerformed(ActionEvent e) {
        	 if(e.getActionCommand() == "Connect Video") { 
        		 	if(LinkListPart.isReset) {
        		 		x = 0;
        		 	}
        		 	ArrayList<ArrayList<Integer>> tmp1 = new ArrayList<ArrayList<Integer>>(FirstVideo.linkBook);
        	    	LinkListPart.allLinks.put(x, tmp1);
        	    	FirstVideo.linkBook.clear();
        	    	
        	    	ArrayList<Integer> tmp2 = new ArrayList<Integer>(FirstVideo.linkBookFrame);
        	    	LinkListPart.allFirstVideoFrames.put(x, tmp2); 
        	    	FirstVideo.linkBookFrame.clear();
        	    	
        	    	LinkListPart.allSndVideoFiles.put(x, ActionPart.SecondaryVideoDir.getName());
        	    	LinkListPart.allSndVideoFrames.put(x, SecondVideo.linkFrame());
        	    	
        	    	
        	    	ArrayList<ArrayList<Double>> tmpError = new ArrayList<ArrayList<Double>>(totalError(tmp1, tmp2)); 
        	    	LinkListPart.allError.put(x, tmpError);
        	    	
        	    	
        	    	LinkListPart.addLink("New Link " + (++x));      	    	
        	    	ConnectVideoButton.setEnabled(false);
        	    	
        	    	if(!SaveFileButton.isEnabled()) {
        	    		SaveFileButton.setEnabled(true);
        	    	}
        	    	
        	  }
        	 else if(e.getActionCommand() == "Save File") {
        		 	try {        		 		
        		 		File f = new File(ActionPart.PrimaryVideoDir.getCanonicalPath() + "\\meta.txt");
						FileOutputStream fos=new FileOutputStream(f);
						OutputStreamWriter osw=new OutputStreamWriter(fos);
						BufferedWriter bw=new BufferedWriter(osw);
						for(int i = 0; i < LinkListPart.allSndVideoFiles.size(); i++) {
							bw.write(LinkListPart.allSndVideoFiles.get(i) + " ");
							bw.write(LinkListPart.allSndVideoFrames.get(i) + " ");
							ArrayList<ArrayList<Integer>> tmp1 =  LinkListPart.allLinks.get(i);
							ArrayList<Integer> tmp2 = LinkListPart.allFirstVideoFrames.get(i);
							for(int j = 0 ; j < tmp2.size(); j++) {
								bw.write(tmp2.get(j)+ " ");
								ArrayList<Integer> tmp3 = tmp1.get(j);
								for(int z = 0; z < 4; z++) {
									bw.write(tmp3.get(z)+ " ");
								}
								
							}
							
							bw.newLine();
						}
						
						
						bw.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {						
						e1.printStackTrace();
					}
        	 }
        }
        
        public ArrayList<ArrayList<Double>> totalError(ArrayList<ArrayList<Integer>> links, ArrayList<Integer> frame) {
	  		ArrayList<ArrayList<Double>> totalError = new ArrayList<ArrayList<Double>>();
	  		for(int i =0; i < frame.size(); i++) {
	  			if((i+1) < frame.size()) {
	  				int frame1 = frame.get(i);
	  				int frame2 = frame.get(i+1);
	  				ArrayList<Integer> a1 = links.get(i);
	  				ArrayList<Integer> a2 = links.get(i+1);
	  				double x = (double)(a2.get(0) - a1.get(0)) / (double)(frame2 - frame1);
	  				double y = (double)(a2.get(1) - a1.get(1)) / (double)(frame2 - frame1); 
	  				double width = (double)(a2.get(2) - a1.get(2)) / (double)(frame2 - frame1);
	  				double height =(double)(a2.get(3) - a1.get(3)) / (double)(frame2 - frame1);				  		
			  		ArrayList<Double> error = new ArrayList<Double>();
			  		error.add(x); error.add(y); error.add(width);error.add(height);
			  		totalError.add(new ArrayList<Double>(error));				  						  		
	  			}
	  				  			
	  		}
	  		return totalError;
  }
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
	static ArrayList<File> rgbFiles = null;
	static JLabel frameLabel;
    //JLabel videoPart;
	static Draw videoPart;
    
    public static int FirstProcess;
    public static ArrayList<ArrayList<Integer>> linkBook = new ArrayList<ArrayList<Integer>>(); 
    public static ArrayList<Integer> linkBookFrame = new ArrayList<Integer>();
    
    
    static JSlider videoProgressSlider;
    
    public static boolean isVisable = false;
    public static boolean isCurrentVisable = false;
    public static boolean isLoaded = false;
    
    
    public static int index;
    public static int[] oneFrameLink;
    public static int[] oneFrameError;
    
    public static int selectLink;
    
    public FirstVideo() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        
        
        videoPart = new Draw();
        videoPart.setMaximumSize(new Dimension(352, 288));
        videoPart.setPreferredSize(new Dimension(352, 288));
        videoPart.setBorder(BorderFactory.createTitledBorder(""));
        videoPart.setAlignmentX(CENTER_ALIGNMENT);
        
        videoProgressSlider = new JSlider(JSlider.HORIZONTAL, 1, 9000, 1) {
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

        frameLabel = new JLabel("Frame 0000");
        frameLabel.setAlignmentX(CENTER_ALIGNMENT);

    	add(videoPart);
    	addMouseListener(videoPart);
    	addMouseMotionListener(videoPart);
    	
    	
        //add(videoPart);
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

            rgbFiles.sort((f1, f2) -> f2.getName().compareTo(f1.getName()));
        }
        
        isLoaded = true;
       
        ControlPart.SaveFileButton.setEnabled(false);
        ControlPart.ConnectVideoButton.setEnabled(false);
        LinkListPart.clear();
        
        setFirstFrame(1);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    	
        JSlider source = (JSlider)e.getSource();
        FirstProcess = source.getValue();
        frameLabel.setText("frame "+String.format("%04d", FirstProcess));      
               	
        if (rgbFiles != null && rgbFiles.size() >= FirstProcess) {
            plotRGBFile(rgbFiles.get(FirstProcess-1));
            visableRectangle();
        }
        
        
    }
    private void setFirstFrame(int frame) {
    	FirstProcess = frame;
    	videoProgressSlider.setValue(frame);
    	frameLabel.setText("frame "+String.format("%04d", frame));
    	if (rgbFiles != null && rgbFiles.size() >= FirstProcess) {
    		plotRGBFile(rgbFiles.get(FirstProcess-1));   		
    	}
    }
    

    public static void setFrame(int frame) {
    	FirstProcess = frame;
    	changeFrame(FirstProcess);
    }
    public static void changeFrame(int frame) {
    	JSlider source = videoProgressSlider;
    	videoProgressSlider.setValue(frame);
    	frameLabel.setText("frame "+String.format("%04d", frame));
    	if (rgbFiles != null && rgbFiles.size() >= FirstProcess) {
    		plotRGBFile(rgbFiles.get(FirstProcess-1));
    		visableRectangle();
    	}
    }
    
    private static void plotRGBFile(File rgbFile) {
        BufferedImage img = ImageDisplay.GetImage(rgbFile);
        if (img != null) {
            videoPart.setIcon(new ImageIcon(img));
                     
        }
 
    }
    
    private static void visableRectangle() {
    	isCurrentVisable = false;
    	isVisable = false;
    	index = 0;
    		   	
    	boolean flag = false;
        for(int i = 0; i < linkBookFrame.size(); i++) {  
        	 if((i + 1) < linkBookFrame.size()) {
        		int x = linkBookFrame.get(i);
        		int y = linkBookFrame.get(i+1);
             	if(FirstProcess == x || (FirstProcess < y && FirstProcess > x)) {
             		isCurrentVisable = true;
             		index = i;
             		flag = true;
             	}
        		 
        	 }
        	 else if((i + 1) >= linkBookFrame.size()) {
        		int x = linkBookFrame.get(i);        		
              	if(FirstProcess == x) {
              		isCurrentVisable = true;
              		index = i;
              		flag = true;
              	}
        		 
        		 
        	 }
        	
        }
        
        Set<Integer> v = LinkListPart.allFirstVideoFrames.keySet();
        oneFrameLink = new int[v.size()];
        oneFrameError = new int[v.size()];
        Arrays.fill(oneFrameLink, -1);
        Arrays.fill(oneFrameError, -1);
        for(int z : v) {
        	ArrayList<Integer> oneFrame = LinkListPart.allFirstVideoFrames.get(z);        	       	
        	 for(int i = 0; i < oneFrame.size(); i++) { 
        		 if((i + 1) < oneFrame.size()) {
        			 int x = oneFrame.get(i);
        			 int y = oneFrame.get(i+1);
        			 if(FirstProcess == x || (FirstProcess > x && FirstProcess < y)) {
                  		isVisable = true;
                  		oneFrameLink[z] = i; 
                  		oneFrameError[z] = FirstProcess - x;
                  		flag = true;
                  		break;
                  	}
        		 }
        		 else if((i + 1) >= oneFrame.size()) {
        			 int x = oneFrame.get(i);
        			 if(FirstProcess == x) {
        				 isVisable = true;
                   		oneFrameLink[z] = i;    
                   		//oneFrameError[z] = FirstProcess - x;
                   		flag = true;
                   		break;
        			 }
        		 }          	
             }
        }
        if(!flag) {
        	videoPart.cleaner(videoPart.getGraphics());
        }
    }
    
    public static void superAdd(ArrayList<Integer> t, int frame) {
    	Collections.sort(linkBookFrame, new Comparator<Integer>() {
    			@Override
                public int compare(Integer o1, Integer o2) {                    
                    return o1-o2;                   
                }
    		});
    	int index = linkBookFrame.indexOf(frame);
    	
    	linkBook.add(index, t);
    }
}

class SecondVideo extends JPanel implements ChangeListener {
	static ArrayList<File> rgbFiles = null;
    static JLabel frameLabel;
    static JLabel videoPart;
    static JSlider videoProgressSlider;
    private static int secondFrame;
    
    public static boolean isLoaded = false;
    
    public SecondVideo() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        videoPart = new JLabel();
        videoPart.setMaximumSize(new Dimension(352, 288));
        videoPart.setPreferredSize(new Dimension(352, 288));
        videoPart.setBorder(BorderFactory.createTitledBorder(""));
        videoPart.setAlignmentX(CENTER_ALIGNMENT);


        videoProgressSlider = new JSlider(JSlider.HORIZONTAL, 1, 9000, 1) {
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

        frameLabel = new JLabel("Frame 0000");
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

            rgbFiles.sort((f1, f2) -> f2.getName().compareTo(f1.getName()));
        }
        
        setFirstFrame(1);
        
        isLoaded = true;
       
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        secondFrame = source.getValue();
        frameLabel.setText("frame "+String.format("%04d", secondFrame));

        if (rgbFiles != null && rgbFiles.size() >= secondFrame) {
            plotRGBFile(rgbFiles.get(secondFrame-1));
        }
    }

    
    private static void setFirstFrame(int frame) {
    	secondFrame = frame;
    	videoProgressSlider.setValue(frame);
    	frameLabel.setText("frame "+String.format("%04d", frame));
    	if (rgbFiles != null && rgbFiles.size() >= secondFrame) {
    		plotRGBFile(rgbFiles.get(secondFrame-1));   		
    	}
    }
    
    public static int linkFrame() {
        return secondFrame;
    }
    
    
    public static void setFrame(int frame) {
    	secondFrame = frame;
    	changeFrame(secondFrame);
    }
    public static void changeFrame(int frame) {
    	JSlider source = videoProgressSlider;
    	videoProgressSlider.setValue(frame);
    	frameLabel.setText("frame "+String.format("%04d", frame));
    	if (rgbFiles != null && rgbFiles.size() >= secondFrame) {
    		plotRGBFile(rgbFiles.get(secondFrame-1));
    	}
    }
    
    private static void plotRGBFile(File rgbFile) {
        BufferedImage img = ImageDisplay.GetImage(rgbFile);
        if (img != null) {
            videoPart.setIcon(new ImageIcon(img));
        }
    }
}

class ActionPart extends JPanel implements ListSelectionListener {
    public JList actionList;
    public DefaultListModel listModel;

    public static final String submitString = "Select a Area";
    public JButton submitButton;
    public JButton openDirButton;

    public static File PrimaryVideoDir = null, SecondaryVideoDir = null;

    public JFileChooser fc;
    JLabel directoryLabel;

    VideoPart videoPart;

    public static boolean isCreateLink = false;
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
                } else if (index == 1) {
                    SecondaryVideoDir = fc.getSelectedFile();
                    System.out.println("Import Secondary Video Directory: " + SecondaryVideoDir);
                    directoryLabel.setText(String.valueOf(SecondaryVideoDir));
                    videoPart.VideoPart2.LoadVideo(SecondaryVideoDir);
                }
            }
        }
    }

    class ActionSubmitListener implements ActionListener {
    	int x = 0;
        public void actionPerformed(ActionEvent e) {
        	if(FirstVideo.isLoaded) {
            	isCreateLink = true;
            }else {
            	isCreateLink = false;
            }
            //int index = actionList.getSelectedIndex();

        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // whether import directory
            int index = actionList.getSelectedIndex();
            openDirButton.setEnabled(index != 2);

            if (index == 0) {
                submitButton.setEnabled(false);
                isCreateLink = false;
            } else if (index == 1) {
                submitButton.setEnabled(false);
                isCreateLink = false;
            } else {
            	if(FirstVideo.isLoaded && SecondVideo.isLoaded) {
            		submitButton.setEnabled(true);   
            	}else {
            		submitButton.setEnabled(false);
            	}
            }
        }
    }
}

class LinkListPart extends JPanel implements ListSelectionListener {
    private JList linkList;
    private static DefaultListModel listModel;

    private static final String reNameString = "Set Name";
    private JButton setNameButton;
    private JTextField newName;
    public static boolean isReset = false;
    
    public static Map<Integer,ArrayList<ArrayList<Double>>> allError;
    public static Map<Integer,ArrayList<ArrayList<Integer>>> allLinks; 
    public static Map<Integer,ArrayList<Integer>> allFirstVideoFrames;   
    public static Map<Integer,String> allSndVideoFiles;
    public static Map<Integer,Integer> allSndVideoFrames;
     
    
    
    public LinkListPart () {
    	
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        listModel = new DefaultListModel();
        

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
        
        if(!isReset) {
        	int indexOfLinks = linkList.getSelectedIndex();
            
            ArrayList<Integer> first = LinkListPart.allFirstVideoFrames.get(indexOfLinks);
            FirstVideo.setFrame(first.get(0));
            
            int second = LinkListPart.allSndVideoFrames.get(indexOfLinks);
            SecondVideo.setFrame(second);
            
            FirstVideo.selectLink = indexOfLinks;
        }else {
        	isReset = false;
        }
        
       
    }
    
    public static void addLink(String name) {
    	listModel.addElement(name);
    	
    }
    public static void clear() {
    	allError = new HashMap<Integer,ArrayList<ArrayList<Double>>>();
    	allLinks = new HashMap<Integer,ArrayList<ArrayList<Integer>>>(); 
        allFirstVideoFrames= new HashMap<Integer,ArrayList<Integer>>();   
        allSndVideoFiles = new HashMap<Integer,String>();
        allSndVideoFrames = new HashMap<Integer,Integer>();
        listModel.removeAllElements();
        isReset = true;
    }
}

class Draw extends JLabel implements MouseListener, MouseMotionListener {
	  //parameters for MouseListener & MouseMotionListener
	  int x1 = 0;
	  int y1 = 0;
	  int x2 = 0;
	  int y2 = 0;
	  
	  //global final area for information transfer
	  ArrayList<Integer> finalArea;
	  
	  public void paint(Graphics g) {
		super.paint(g);		
		
		//drawing for 1 rect in creating link
	    if(ActionPart.isCreateLink) { 
	    	
	    	g.setColor(Color.YELLOW);
	    
	    	if(x2 < x1) {
	    		g.drawRect(x2, y2, Math.abs(x2-x1), Math.abs(y2 - y1));
	    	}
	    	else {
	    		g.drawRect(x1, y1, Math.abs(x2-x1), Math.abs(y2 - y1));
	    	}	    	    	
	    	
	    	
	    }
	    
	  //drawing for creating link
	    if(FirstVideo.isCurrentVisable) {
	    	
	    	//for(int i = 0; i < FirstVideo.index.size(); i++) {
	    		int x = FirstVideo.index;
	    		ArrayList<Integer> frame = FirstVideo.linkBook.get(x); 
	    		superPaintYellow(g,frame);
	    	//}
	    }
	    
	  //drawing for other links
	    if(FirstVideo.isVisable) {	    	
	    		for(int i = 0; i< FirstVideo.oneFrameLink.length;i++) {
	    			
	    			ArrayList<ArrayList<Integer>> oneframe = LinkListPart.allLinks.get(i);

	    			ArrayList<ArrayList<Double>> oneError = LinkListPart.allError.get(i);
	    				int x = FirstVideo.oneFrameLink[i];
	    				if(x != -1) {
	    					ArrayList<Integer> frame = oneframe.get(x);
	    					int frameError = FirstVideo.oneFrameError[i];
	    					if(frameError == -1) {
	    						ArrayList<Double> error = new ArrayList<Double>();
	    						error.add(0.0);error.add(0.0);error.add(0.0);error.add(0.0);
	    						if(FirstVideo.selectLink == i) {
					    			hyperSuperPaint(g,frame,error,0,false);
					    		}else {
					    			hyperSuperPaint(g,frame,error,0,true);
					    		}
	    					}else {
	    						ArrayList<Double> error = oneError.get(x);
	    						
	    						if(FirstVideo.selectLink == i) {
					    			hyperSuperPaint(g,frame,error,frameError,false);
					    		}else {
					    			hyperSuperPaint(g,frame,error,frameError,true);
					    		}
	    					}
				    		
	    				}
			    					    		
			    	
	    		}
	    	
	    }
	  }
	 
	  //yellow repaint
	  public void superPaintYellow(Graphics g, ArrayList<Integer> area) {
		  		//super.paint(g);
		  		
		  			g.setColor(Color.YELLOW);		   		    	
			    	g.drawRect(area.get(0), area.get(1), area.get(2), area.get(3));
			    	
		    	
		    	  
		 
		  }
	  
	  public void hyperSuperPaint(Graphics g, ArrayList<Integer> area, ArrayList<Double> error, int frameError, boolean flag) {
	  		//super.paint(g);
	  		
		  		int x = area.get(0) + (int)(error.get(0) * frameError);
		  		int y = area.get(1) + (int)(error.get(1) * frameError); 
		  		int width = area.get(2) + (int)(error.get(2) * frameError); 
		  		int height = area.get(3) + (int)(error.get(3) * frameError);
		  		if(flag) {
		  			g.setColor(Color.BLUE);		   		    	
			    	g.drawRect(x, y, width, height);
		  		
		  		}
		  		else {
		  			g.setColor(Color.RED);		   		    	
			    	g.drawRect(x, y, width, height);
		  		}
	    	
	    	  
	 
	  }
	  
	 
	 
	  
	  public void cleaner(Graphics g) {
	  		super.paint(g);		    	    		    	
	    	
	  }

	  @Override
	  public void mouseClicked(MouseEvent e) {
		 
	  }
	 
	  @Override
	  public void mousePressed(MouseEvent e) {
		  if(ActionPart.isCreateLink == true) {  
			  x1 = e.getX();
			  y1 = e.getY();
		  }
		  
	  }
	 
	  @Override
	  public void mouseReleased(MouseEvent e) {
		  
		  if(ActionPart.isCreateLink == true) {  
			  finalArea = new ArrayList<Integer>();
			  if(x2 < x1) {
				  finalArea.add(x2);
				  finalArea.add(y2);
				  finalArea.add(Math.abs(x2-x1));
				  finalArea.add(Math.abs(y2 - y1));
			  }
			  else {
				  finalArea.add(x1);
				  finalArea.add(y1);
				  finalArea.add(Math.abs(x2-x1));
				  finalArea.add(Math.abs(y2 - y1));
			  }
		  
		  if(FirstVideo.linkBookFrame.contains(FirstVideo.FirstProcess)) {
			  int x = FirstVideo.linkBookFrame.indexOf(FirstVideo.FirstProcess);
			  FirstVideo.linkBook.remove(x);
			  FirstVideo.linkBook.add(x, finalArea);		  
			
		  }
		  else {
			  FirstVideo.linkBookFrame.add(FirstVideo.FirstProcess);
			  FirstVideo.superAdd(finalArea, FirstVideo.FirstProcess);
		  }
		  if(!ControlPart.ConnectVideoButton.isEnabled()) {
			  ControlPart.ConnectVideoButton.setEnabled(true);
		  }		  
		  ActionPart.isCreateLink = false;
		  }
	  }
	 
	  @Override
	  public void mouseEntered(MouseEvent e) {
	 
	  }
	 
	  @Override
	  public void mouseExited(MouseEvent e) {
	  }

	@Override
	public void mouseDragged(MouseEvent e) {
		if(ActionPart.isCreateLink == true) {  
			x2 = e.getX();
			y2 = e.getY();
			this.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		
	}
	}
