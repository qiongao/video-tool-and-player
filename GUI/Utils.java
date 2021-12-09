package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Utils {
    public static void PlotRGBFile(File rgbFile, JLabel videoLabel) {
        BufferedImage img = ImageDisplay.GetImage(rgbFile);
        if (img != null) {
            videoLabel.setIcon(new ImageIcon(img));
        }
    }

    public static void SetFrameShow(int frame, ArrayList<File> rgbFiles, JLabel videoLabel) {
        if (rgbFiles != null && rgbFiles.size() >= frame && frame > 0) {
            Utils.PlotRGBFile(rgbFiles.get(frame-1), videoLabel);
        }
    }

    public static ArrayList<ArrayList<Double>> totalError(ArrayList<ArrayList<Integer>> links, ArrayList<Integer> frame) {
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
