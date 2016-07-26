package capsensor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;


import info.monitorenter.gui.chart.*;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;
import jssc.SerialPortException;

/**
 * Java GUI for displaying the capacitance values read by capacitive sensor
 * 
 * @author Caleb Ng (2015) for Tesseract Technologies
 *
 * Edited by Dilan Ustek (2016)
 */
public class CapMatrix extends JPanel {
	private static final long serialVersionUID = 1L;

	// debug variables
	protected static String debugText = new String();
	protected static final String DEBUG_COM_UNAVAILABLE = "Unable to connect to Arduino...\nThe serial may be incorrect, not connected, or busy.";
	protected static final String DEBUG_WAIT_CALIBRATE = "Calibrating baseline...";
	private static final String DEBUG_START = "Application started.";

	// GUI Max and Min Range
	protected static double minRange = 0.5;
	protected static double maxRange = 1.5;

	// Timing variables
	private static int indexRead = 0;
	private static DateFormat indexStart;
	private static DateFormat indexStop;
	private static int indexMax = 1000;



	public static void main(String[] a) {
		// Try reading application settings from file
		try {
			System.out.println("Reading file");
			AppSettings.readFile(AppSettings.fileName);
			FilterUtil.numDataPts = AppSettings.numColumns * AppSettings.numRows; // TODO: is it needed?
		} catch (IOException e) {
			System.out.println("Unable to read settings from file.");
		}
		// Prepare GUI elements
		FrameUtil.frame = new JFrame();
		FrameUtil.frame.setTitle(FrameUtil.frameTitle);
		FrameUtil.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		FrameUtil.frame.setLayout(new GridLayout(numRows,numColumns));
		FrameUtil.frame.setLayout(new GridBagLayout());
		FrameUtil.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(PortUtil.serialPort != null) {
					try {
						PortUtil.serialPort.closePort();
					} catch (SerialPortException e1) {
//						e1.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
		debugText = DEBUG_START;
		prepareDataStructures();
		FrameUtil.addComponentsToFrame();
		FrameUtil.frame.pack();
//		f.setResizable(false);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FrameUtil.frame.setVisible(true);
	}
	
	/**
	 * Function for preparing data structures for initial run-through
	 */
	private static void prepareDataStructures() {
		// Prepare hash map
		DataController.gridAddressMap = new HashMap<Integer, int[]>();
		for(int i=0; i<AppSettings.numRows; i++) {
			for(int j=0; j<AppSettings.numColumns; j++) {
				System.out.println("Adding address: " + Integer.parseInt(AppSettings.gridAddress[(i*AppSettings.numRows) + j],2));
//				gridAddressMap.put(AppSettings.gridAddress[(i*AppSettings.numRows) + j], new int[]{i,j});
				DataController.gridAddressMap.put(Integer.parseInt(AppSettings.gridAddress[(i*AppSettings.numRows) + j],2), new int[]{i,j});
			}
		}		
		// Prepare trace array
//		IAxis chartAxis = new IAxis(new LabelFormatterDate(DateFormat.getDateInstance()));
		for(int i=0; i<FilterUtil.numDataPts; i++) {
			DataController.dataTrace.add(new Trace2DLtd(50));
			DataController.dataTrace.get(i).setColor(Color.BLUE);
			DataController.dataTrace.get(i).setName("");
//			DataController.chartsArray.add(new ChartObject(dataTrace.get(i)));
			DataController.chartsArray.add(new Chart2D());
			DataController.chartsArray.get(i).addTrace(DataController.dataTrace.get(i));
			DataController.chartsArray.get(i).setMinimumSize(new Dimension(AppSettings.widthRect, AppSettings.heightRect));
			DataController.chartsArray.get(i).setPreferredSize(new Dimension(AppSettings.widthRect, AppSettings.heightRect));
			// Set axis format
			IAxis xAxis = DataController.chartsArray.get(i).getAxisX();
			xAxis.setFormatter(new LabelFormatterDate(new SimpleDateFormat("HH:mm:ss")));
			xAxis.setTitle("Time");
//			chartAxis.setFormatter(new LabelFormatterDate(new SimpleDateFormat("HH:mm:ss:SSS")));
			IAxis yAxis = DataController.chartsArray.get(i).getAxisY();
			//yAxis.setTitle("Capacitance");

			//set constant Y axis range
			IRangePolicy rangePolicy = new RangePolicyFixedViewport(new Range(minRange, maxRange));
			DataController.chartsArray.get(i).getAxisY().setRangePolicy(rangePolicy);

		}
	}
	



	

}