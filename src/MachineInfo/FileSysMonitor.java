package MachineInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Implement a monitor to get the information of the file system
 * in each time interval.
 * @author dell-pc
 */
public class FileSysMonitor {
	private int timeInterval;
	private FileSysIndicator fileSysIndicator;
	private Timer timer;
	
	/**
	 * @param timeInterval in seconds
	 */
	public FileSysMonitor(int timeInterval) {
		this.timeInterval = timeInterval;
		this.timer = new Timer();
		this.fileSysIndicator = new FileSysIndicator(false);
		timer.schedule(this.fileSysIndicator, 0, this.timeInterval*1000);
	}
	
	/**
	 * Stop the monitor and save log
	 * @return logs
	 */
	public void cancelMonitor(){
		try {
			if(System.in.read() == 'E'){
				this.timer.cancel();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write log to a csv file
	 * @param filePath
	 */
	public void logWritor(String filePath){
		// Create a new file
		File file = new File(filePath);
		try {
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			// Write log to a csv file
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			// Write title
			bw.write("part_name,time,queue,read,write,service_time\n");
			for(String log:this.fileSysIndicator.getRecords()){
				bw.write(log);
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("Error in create new file!");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// Test
		FileSysMonitor fm = new FileSysMonitor(3);
		fm.cancelMonitor();
		fm.logWritor("E:\\file_monitor\\log.csv");
	}

}
