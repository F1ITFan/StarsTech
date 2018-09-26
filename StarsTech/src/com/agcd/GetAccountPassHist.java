package com.agcd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.IdentityService;
import sailpoint.object.Application;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.QueryOptions;

import sailpoint.tools.GeneralException;

import sailpoint.services.standard.junit.SailPointConnectionFactory;
import sailpoint.services.standard.junit.SailPointJUnitTestHelper;


public class GetAccountPassHist // extends SailPointJUnitTestHelper
{
	private SailPointContext context;
	
    static Logger logger = Logger.getLogger("sts.form.script.getServerListFromApplicationGroup");

/*------------------------------------------------------------------------------------------------------
 * 
 *------------------------------------------------------------------------------------------------------*/
public GetAccountPassHist(SailPointContext ctx) throws Exception 
{
 //        super("spadmin", "admin", AutoClose.AfterClass);
 //	super("spadmin", "admin");

	if (ctx != null)
		context = ctx;
	
}
/*------------------------------------------------------------------------------------------------------
 * 
 *------------------------------------------------------------------------------------------------------*/
public String getAccountPassHist(String applicationId, Identity identity, SailPointContext context) 
		throws GeneralException
{
	String passHist = null;
	IdentityService  identityService = null;
	Link             nativeApp = null;
	
	identityService = new IdentityService(context);
	
	nativeApp = identityService.getLinkById(identity, applicationId);
	
	if (nativeApp != null)
	  passHist = nativeApp.getPasswordHistory(); //--- return CSV list of passwords ---
	
	return passHist;
}  //--- End of method '' ---

/*------------------------------------------------------------------------------------------------------
 *  getServerListFromApplicationGroup
 *  
 *   STS' applications are group in applicationGroups (Server Types : 'Windows Server OPS',
 *   'Windows Server DBA', 'Linux 1', 'Linux 2', any string...
 *   
 *   So this method output all IIQ's 'applications' where match the applicationGroup provided...
 *   
 *   The 'applicationGroup' can be implemented as an extended Application Attribute or as a 'Profile Class'
 *   in the Application->Details tab...
 *------------------------------------------------------------------------------------------------------*/
public List<String> getServerListFromApplicationGroup (String applicationGroup)
{
	boolean            proceed = true;
	ArrayList<String>  applicationNames = null;
	StringBuffer       csv_list = null;
	QueryOptions   	   queryOptions = null;
	Filter             filter = null;
	List<Application>  applicationList = null;
	
	
    logger.debug (" [ getServerListFromApplicationGroup ] Entering ...appGroup = '" + applicationGroup + "'");

	if (applicationGroup != null)
	{
	  filter = Filter.eq("applicationGroup", applicationGroup);
	  queryOptions = new QueryOptions(filter);
	  
	  logger.debug (" [ getServerListFromApplicationGroup ] queryOptions  = '" + queryOptions.toString() + "'");

	   try
	   {
	   
	     applicationList = context.getObjects(Application.class, queryOptions);


	     if (applicationList != null && applicationList.size() > 0)
	     {
	    	for (Application app : applicationList)
	    	{
	    		if (applicationNames == null)
	  	          applicationNames = new ArrayList<String>();

	    		applicationNames.add(app.getName());
	    	}
	     }
	     else
	       logger.debug (" [ getServerListFromApplicationGroup ] The Application list matching " +
	    		   		 "'applicationGroup = '" + applicationGroup + "' is NULL !");
	    	 
	   }
	   catch (GeneralException e)
	   {
		  proceed = false;
  	      logger.error (" [ getServerListFromApplicationGroup ] SailPointContext (" +  applicationGroup +
  	    		  		") threw an exception : " + e.toString());
		   
	   }
	}
	
	if (proceed == true)
	{
		if (applicationNames != null)
		{
		  for (String value : applicationNames)	
		  {
			  if (csv_list == null)
				 csv_list = new StringBuffer();
			  
			  csv_list.append(value + ",");
		  }
		  
	      logger.debug (" [ getServerListFromApplicationGroup ] Leaving ... ApplicationNames list (CSV) = '" +
	    		  		csv_list + "'");
		}
		else
	      logger.debug (" [ getServerListFromApplicationGroup ] Leaving ... ApplicationNames list is NULL !" );
	}	
		
	return applicationNames;
	
}  //--- End of method '' ---

/*------------------------------------------------------------------------------------------------------
 *  main
 *------------------------------------------------------------------------------------------------------*/
public static void main (String args[])
{
	GetAccountPassHist  getAcct = null;
	ArrayList<String>   appsList = null;
	SailPointContext context = null;
	
	
	SailPointConnectionFactory factory = SailPointConnectionFactory.getInstance(); 
	
	try
	{
	   context = factory.getContext("spadmin", "admin");
	}
	catch (Exception e) 
	{
		logger.debug("new SailPointConnectionFactory.getInstance() threw : " + e.toString());
		e.printStackTrace();
	}
	
	try
	{
		getAcct = new GetAccountPassHist(context);
		
		appsList = (ArrayList<String>) getAcct.getServerListFromApplicationGroup("Linux 1");
		
		for (String appName : appsList)
		{
			logger.info(" appName '" + appName + "'");
		}
	} 
	catch (Exception e)
	{
		// TODO Auto-generated catch block
		logger.debug("new GetAccountPassHist threw : " + e.toString());
	}
	
	
	//--- Close the SailPointContext ---
	logger.debug("Closing SailPoint context ... ");
	factory.closeContext();
	
	
	
}  //--- End of method 'main' ---

}  //--- End of class 'GetAccountPassHist' ---

