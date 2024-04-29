package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import dao.AdminDao;
import dao.ClientDao;
import dao.ClientEventDao;
import dao.ClientServiceDao;
import dao.ServiceDao;
import dto.Admin;
import dto.Client;
import dto.ClientEvent;
import dto.ClientService;
import dto.EventType;
import dto.Service;

public class EventManagement 
{
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("amit");
	EntityManager em = emf.createEntityManager();
	EntityTransaction et = em.getTransaction();
	
	AdminDao adao = new AdminDao();
	ServiceDao sdao = new ServiceDao();
	ClientDao cdao = new ClientDao();
	ClientEventDao ceDao = new ClientEventDao();
	ClientServiceDao csDao = new ClientServiceDao();
	
	Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args)
	{
		EventManagement em = new EventManagement();
    //	System.out.println(em.saveAdmin());
	//	System.out.println(em.saveService());
	//	System.out.println(em.updateService()); 
	//  System.out.println(em.deleteService());	
	//	System.out.println(em.signupClient());
	//	System.out.println(em.createClientEvent());
	//	System.out.println(em.saveClientService());
		System.out.println(em.removeEventService());
		
	}
	
	public Admin adminLogin()
	{
		System.out.println("Enter the Admin email:");
		String email = sc.next();
		System.out.println("Enter the Admin Password");
		String password = sc.next();
		
		String jpql = "select a from Admin a";
		Query query = em.createQuery(jpql);
		
		List<Admin> admins = (List<Admin>) query.getResultList();
		for(Admin a : admins)
		{
			if(a.getAdminEmail().equals(email))
			{
				return a;
			}
		}
		return null;
	}
	
	public Admin saveAdmin()
	{
		System.out.println("---------------------- SIGNUP ADMIN ----------------------");
		Admin admin = new Admin();
		System.out.println("Enter the Admin name:");
		admin.setAdminName(sc.next()); 
		System.out.println("Enter the Admin email:");
		admin.setAdminEmail(sc.next());
		System.out.println("Enter the Admin password:");
		admin.setAdminPassword(sc.next());
		System.out.println("Enter the Admin contact number:");
		admin.setAdminContact(sc.nextLong());
		
		Admin newAdmin = adao.saveAdmin(admin);
		if(newAdmin != null)
		{
			System.err.println("You are a new Admin..!");	
		}
		else
			System.err.println("try again!");
		
		return newAdmin;
	}
	
	public Service saveService()
	{
		System.out.println("---------------------- ADD SERVICES ----------------------");
		Service service = new Service();
		System.out.println(("Enter the Service name:"));
		service.setServiceName(sc.next());
		System.out.println(("Enter the Service cost per day:"));
		service.setServiceCostPerDay(sc.nextDouble());
		System.out.println(("Enter the Service cost per person:"));
		service.setServiceCostPerPerson(sc.nextDouble());
		
		Service newService =  sdao.saveService(service);
		if(newService != null)
		{
			System.err.println("Service added successfully...");
			
			System.err.println("If you want to add more services press 1!");
			int num = sc.nextInt();
			
			if(num == 1)
			{
				saveService();
			}
			else
				return newService;
		}
		else
			System.err.println("Service added failed...");
		
		return newService;
	}
	
	public List<Service> getAllServices()
	{
			Query query = em.createQuery("select s from Service s");
			List<Service> services = (List<Service>) query.getResultList();
			if(services != null)
			{
				return services;
			}
			return null;
	}
	
	public String updateService()
	{
		System.out.println("Enter Admin credentials to proceed....!");
		Admin exAdmin = adminLogin();
		
		if(exAdmin != null)
		{
			List<Service> services = getAllServices();
			
			System.out.println("------------------------- Choose serviceId from below to update -------------------------");
			System.out.println();
			System.out.println("serviceId     "+"serviceName     "+"cost_per_person     "+"cost_per_day");
			for(Service s : services)
			{
				System.out.println("  "+s.getServiceId()+"          "+s.getServiceName()+"          "+s.getServiceCostPerPerson()+"          "+s.getServiceCostPerDay());
			}
				int id = sc.nextInt();
				Service toUpdated = sdao.findService(id);
				System.out.println("Enter updated cost per person");
				double costPerPerson = sc.nextDouble();
				System.out.println("Enter updated cost per day");
				double costPerDay = sc.nextDouble();
				toUpdated.setServiceCostPerPerson(costPerPerson);
				toUpdated.setServiceCostPerDay(costPerDay);
				
				Service updated = sdao.updateService(toUpdated, id);
				if(updated != null)
				{
					return "Service update success....!";
				}
		}
		return  "Service update failed....!";	
	}
	
	public String deleteService()
	{
		System.out.println("Enter Admin credentials to proceed....!");
		Admin exAdmin = adminLogin();
		
		if(exAdmin != null)
		{
			List<Service> services = getAllServices();
			System.out.println("------------------------- Choose serviceId from below to delete -------------------------");
			System.out.println();
			
			System.out.println("serviceId     "+"serviceName     "+"cost_per_person     "+"cost_per_day");
			for(Service s : services)
			{
				System.out.println("  "+s.getServiceId()+"          "+s.getServiceName()+"          "+s.getServiceCostPerPerson()+"          "+s.getServiceCostPerDay());
			}
			
			int id = sc.nextInt();
			
			List<Service> newList = new ArrayList<Service>();
			
			for(Service s : services)
			{
				if(s.getServiceId() != id)
				{
					newList.add(s);
				}
			}
			
			exAdmin.setServices(newList);
			adao.updateAdmin(exAdmin, exAdmin.getAdminId());
		    Service deleted = sdao.deleteService(id);	
		    
		    if(deleted != null)
		    {
		    	return "Service deleted success....";
		    }
		}
		return "Service deleted failed....";
		
	}
	
	public Client signupClient()
	{
		System.out.println("-------------------------- CLIENT SIGNUP --------------------------");
		Client client = new Client();
		System.out.println("Enter the name:");
		client.setClientName(sc.next());
		System.out.println("Enter the contact:");
		client.setClientContact(sc.nextLong());
		System.out.println("Enter the email:");
		client.setClientEmail(sc.next());
		System.out.println("Enter the password");
		client.setClientPassword(sc.next());
		Client newClient = cdao.saveClient(client);
		
		if(newClient != null)
		{
			System.err.println("Signup success..!");
		}
		else
			System.err.println("Signup failed..!");
		
		return newClient;
	}
	
	
	public Client loginClient()
	{
		System.out.println("-------------------------- CLIENT LOGIN --------------------------");
		System.out.println("Enter the email:");
		String email = sc.next();
		System.out.println("Enter the password:");
		String password = sc.next();
		
		String jpql = "select c from Client c";
		Query query = em.createQuery(jpql);
		
		List<Client> clients = (List<Client>) query.getResultList();
		
		for(Client c : clients)
		{
			if(c.getClientEmail().equals(email))
			{
				return c;
			}
		}
		return null;
	}
	
	public Client createClientEvent()
	{
		System.out.println("-------------------------- CREATE YOUR EVENTS HERE --------------------------");
		Client client = loginClient();
		
		if(client != null)
		{
			ClientEvent clientEvent = new ClientEvent();
			System.out.println("Choose the event:");
			
			EventType eventType[] = EventType.values();
			int i = 1;
			for(EventType et : eventType)
			{
				System.out.println(i++ +"."+ et);
			}
			
			int e = sc.nextInt();
			if(e == 1)
			{
				clientEvent.setEventType(EventType.Weddings);
			}
			if(e == 2)
			{
				clientEvent.setEventType(EventType.Engagement);
			}
			if(e == 3)
			{
				clientEvent.setEventType(EventType.BirthdayParty);
			}
			if(e == 4)
			{
				clientEvent.setEventType(EventType.WelcomeParty);
			}
			if(e == 5)
			{
				clientEvent.setEventType(EventType.CharityEvents);
			}
			if(e == 6)
			{
				clientEvent.setEventType(EventType.Farewell);
			}
			if(e == 7)
			{
				clientEvent.setEventType(EventType.BachelorParty);
			}
			if(e == 8)
			{
				clientEvent.setEventType(EventType.Reunion);
			}
			if(e == 9)
			{
				clientEvent.setEventType(EventType.BabyShower);
			}
			if(e == 10)
			{
				clientEvent.setEventType(EventType.SurpriceEvent);
			}
			
			System.out.println("Enter number of people:");
			clientEvent.setClientEventNoOfPeople(sc.nextInt());
			System.out.println("Enter number of days:");
			clientEvent.setClientEventNoOfDays(sc.nextInt());
			System.out.println("Enter the event location:");
			clientEvent.setClientEventLocation(sc.next());
			
			ClientEvent savedClientEvent = ceDao.saveClientEvent(clientEvent);
			
			clientEvent.setClient(client);
			client.getEvents().add(savedClientEvent);
			Client c = cdao.updateClient(client, client.getClientId());
			if(c != null)
			{
				System.err.println("your event successfully created...!");
			}
			else
				System.err.println("Event created failed...!");
			
			return c;
		}
		return null;	
	}
	
	public Client saveClientService()
	{
		Client client = loginClient();
		
		if(client != null)
		{
			List<Service> adminServices = getAllServices(); 
			System.out.println("------------------------- A to Z services -------------------------");
	        System.out.println();
			
			System.out.println("serviceId     "+"serviceName     "+"cost_per_person     "+"cost_per_day");
			for(Service s : adminServices)
			{
				System.out.println("  "+s.getServiceId()+"          "+s.getServiceName()+"          "+s.getServiceCostPerPerson()+"          "+s.getServiceCostPerDay());
			}
			System.err.println("How many services are you want from above services?");
			int noOfServices = sc.nextInt();
			System.err.println("You're selected "+noOfServices+" services..!");
			
			List<ClientService> clientServices = new ArrayList<>();
			
			for(int j=1;j<=noOfServices;j++)
			{
				System.err.println("Confirm your service-"+j+" to enter the service number from above services");
				int id = sc.nextInt();
				
				Service service = sdao.findService(id);
				
				Query query = em.createQuery("select ce from ClientEvent ce");
				List<ClientEvent> clientEvents = (List<ClientEvent>) query.getResultList();
				
				ClientService clientService = new ClientService();
				
				for(ClientEvent clientEvent : clientEvents)
				{
					if(clientEvent.getClient().getClientId() == (client.getClientId()))
					{
						clientService.setClientServiceName(service.getServiceName());
						clientService.setClientServiceCostPerPerson(service.getServiceCostPerPerson());
						clientService.setClientServiceNoOfDays(clientEvent.getClientEventNoOfDays());
						double totalCostOfAllPeoples = clientEvent.getClientEventNoOfPeople()*service.getServiceCostPerPerson();
						double totalCostOfAllDays = clientEvent.getClientEventNoOfDays()*service.getServiceCostPerDay();
						clientService.setClientServiceCost(totalCostOfAllPeoples+totalCostOfAllDays);
						clientServices.add(clientService);
						csDao.saveClientService(clientService);
						double clientEventCost = clientEvent.getClientEventCost()+clientService.getClientServiceCost();
						clientEvent.setClientEventCost(clientEventCost);
						clientEvent.setClientServices(clientServices);
						ceDao.updateClientEvent(clientEvent, clientEvent.getClientEventId());
					}
				}
			}
			return client;
		}
		return null;
	}
	
	public ClientService removeEventService()
	{
		Client client = loginClient();
		
		if(client != null)
		{
			Query query = em.createQuery("select ce from ClientEvent ce");
			List<ClientEvent> clientEvents = (List<ClientEvent>) query.getResultList();
			
			for(ClientEvent ce : clientEvents)
			{
				if(ce.getClient().getClientId() == client.getClientId())
				{
					List<ClientService> clientServices = (List<ClientService>) ce.getClientServices();
					
					System.out.println("------------------------- Choose serviceId from below to delete -------------------------");
					System.out.println("ServiceId      "+" ServiceName");
					for(ClientService cs : clientServices)
					{
						System.out.println("       "+cs.getClientServiceId()+"       "+cs.getClientServiceName());
					}
					
					int id = sc.nextInt();
					
					for(ClientService cs : clientServices)
					{
						if(cs.getClientServiceId() == id)
						{
							clientServices.remove(cs);
							ce.setClientServices(clientServices);
							
							double clientEventCost = 0;
							for(ClientService cs2 : clientServices)
							{
								clientEventCost += cs2.getClientServiceCost();
							}
							ce.setClientEventCost(clientEventCost);
							ceDao.updateClientEvent(ce,ce.getClientEventId());
							
							ClientService deleted = csDao.deleteClientService(cs.getClientServiceId());
							if(deleted != null)
							{
								System.err.println("service removed successfully..!");
							}
							else
								System.err.println("service remove   failed..!");
							
							return deleted; 
						}
					}
				}
			}
		}
		return null;
	}
}

