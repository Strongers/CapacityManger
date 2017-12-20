package MachineInfo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.sun.org.apache.bcel.internal.generic.NEW;

import sun.security.action.GetBooleanAction;

/**
 * Get the information of the file system one times.
 * @author dell-pc
 */
public class FileSysIndicator extends TimerTask{
	private Sigar sigar; // Sigar object to get the system information
	private FileSystem[] partitions; // Partitions of file system, C D...
	private ArrayList<String> partName; // Names of each partition
	private static Long recordIndex = 0L; // Index of the record
	private ArrayList<String> records; // Records
	private boolean log;
	
	public FileSysIndicator(boolean log) {
		super();
		this.sigar = new Sigar();
		try {
			this.partitions = this.sigar.getFileSystemList();
		} catch (SigarException e) {
			System.out.println("Cannot get partition names!");
			e.printStackTrace();
		}
		this.partName = this.getPartitionList();
		this.records = new ArrayList<String>();
		this.log = log;
	}
	
	/**
	 * Get the names of each partition for initlization.
	 * @return An ArrayList contains all partition names
	 */
	private ArrayList<String> getPartitionList(){
		ArrayList<String> names = new ArrayList<>();
		// For every partition, get the device name
		if (this.partitions.length > 0){
			System.out.print("File system contains: ");
			for (FileSystem part:this.partitions){
				System.out.print(part.getDevName());
				names.add(part.getDevName());
				System.out.print("  ");
			}
			System.out.println();
		}else {
			System.out.println("Cannot get partition names!");
		}
		return names;
	}
	
	/**
	 * Get the target names of the partition.
	 * @param index
	 * @return
	 */
	public String getName(int index) {
		String name = null;
		if (index <= this.partName.size()){
			name = this.partName.get(index);
		}else{
			System.out.println("Index out of boundary!");
		}
		return name;
	}
	
	/**
	 * Get the information of each partition.
	 * @param partName name of the partition
	 * @param log if print log
	 * @return string split by ',', contains partName, index, queues, read(MB), 
	 * 		   write(MB), service time 
	 */
	private void getFileSysInfo(){
		// For each partition in the file system
		FileSystemUsage usage = null;
		String record = "";
		for (int partIdx=0; partIdx<this.partName.size(); partIdx++){
			try {
				usage = this.sigar.getFileSystemUsage(this.partName.get(partIdx));
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// store all info to a array, 
			// contains: queues, read/mb, write/mb, service time
			record += this.partName.get(partIdx).substring(0, 1) + ',';
			record += this.recordIndex;
			record += ',';
			if(usage != null){
				record += usage.getDiskQueue();
				record += ",";
				record += usage.getDiskReadBytes()/(float)(1024*1024);
				record += ",";
				record += usage.getDiskWriteBytes()/(float)(1024*1024);
				record += ",";
				record += usage.getDiskServiceTime();
			}else {
				for(int i=0; i<3; i++){
					record += "null";
					record += ",";
				}
				record += "null";
			}
			record += "\n";
		}
		// print log
		if (this.log) {
			System.out.println(record);
		}
		this.recordIndex++;
		this.records.add(record);
	}
	
	/**
	 * Get all of the records.
	 * @return
	 */
	public ArrayList<String> getRecords(){
		return this.records;
	}
	
	@Override
	public void run(){
		this.getFileSysInfo();
	}
}
