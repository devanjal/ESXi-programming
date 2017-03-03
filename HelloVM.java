package com.sjsu.test;

import java.net.URL;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class HelloVM {

	public static void main(String[] args) throws Exception {

		System.out.println("CMPE283 HW1 from Devanjal Kotia");
		String ip = args[0],login = args[1],password = args[2];
		URL url = new URL("https://" + ip + "/sdk");
		ServiceInstance s = new ServiceInstance(url,login,password,true);
		ManagedEntity[] mge = new InventoryNavigator(s.getRootFolder()).searchManagedEntities("VirtualMachine");
		ManagedEntity[] he = new InventoryNavigator(s.getRootFolder()).searchManagedEntities("HostSystem");
		int i=0;
		for(ManagedEntity hostmanagedEntity : he){
			HostSystem hs = (HostSystem)hostmanagedEntity;
			System.out.println("--------HOST--------");
			System.out.println("Name : "+hs.getName());
			System.out.println("Product Name = "+s.getAboutInfo().getFullName());
			
			Datastore[] ds = hs.getDatastores();
			
			for (int j=0;j <ds.length;j++){
				DatastoreSummary dsm =ds[i].getSummary();
					double cap=(dsm.capacity)/(1024.0*1024.0*1024.0);
					
					double free_space=(dsm.freeSpace)/(1024.0*1024.0*1024.0);

				System.out.println("Datastore["+j+"]: Host Name="+dsm.name+", Capacity = "+cap+" GB, Available Space = "+free_space+" GB");
			}
			
			Network[] nw = hs.getNetworks();
			for(int j=0;j < nw.length;j++)
				System.out.println("Network["+j+"]: Name="+nw[0].getName());
			i++;
		}
		
		i=0;
		for(ManagedEntity hostmanagedEntity : mge){
			VirtualMachine vm = (VirtualMachine)hostmanagedEntity;	
			System.out.println("--------VM--------");
			System.out.println("Name = "+vm.getName());
			VirtualMachineConfigInfo vmcon = vm.getConfig();
			System.out.println("GuestOS = " + vmcon.getGuestFullName());
			System.out.println("Guest state = "+vm.getGuest().guestState);
			System.out.println("Power State = "+ vm.getRuntime().powerState);
			if("poweredOff".equalsIgnoreCase(vm.getRuntime().powerState.toString()))
			{
				
				Task task = vm.powerOnVM_Task(null);
				if(task.waitForMe()==Task.SUCCESS)
			        System.out.println("Power on VM: status = Success");
				else
					System.out.println("Power on VM: status = Failure");
			}
			else
			{
				Task task = vm.powerOffVM_Task();
				if(task.waitForMe()==Task.SUCCESS)
					System.out.println("Power off VM: status = Success");
				else	
					System.out.println("Power on VM: status = Failure");
			}
			
			if(i==0)
			{
				Task task1 = vm.getRecentTasks()[i];
				System.out.println("Task: Target="+vm.getName()+", OP= "+task1.getTaskInfo().getName()+", startTime="+task1.getTaskInfo().getStartTime().getTime());
			}
			else
			{
				Task task1 = vm.getRecentTasks()[i-1];
				System.out.println("Task: Target="+vm.getName()+", OP= "+task1.getTaskInfo().getName()+", startTime="+task1.getTaskInfo().getStartTime().getTime());
			}
			
			
			i++;
		}
		
		s.getServerConnection().logout();
	}
}
