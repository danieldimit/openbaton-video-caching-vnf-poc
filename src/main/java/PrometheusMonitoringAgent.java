
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.openbaton.catalogue.mano.common.monitoring.AbstractVirtualizedResourceAlarm;
import org.openbaton.catalogue.mano.common.monitoring.Alarm;
import org.openbaton.catalogue.mano.common.monitoring.AlarmEndpoint;
import org.openbaton.catalogue.mano.common.monitoring.ObjectSelection;
import org.openbaton.catalogue.mano.common.monitoring.PerceivedSeverity;
import org.openbaton.catalogue.mano.common.monitoring.ThresholdDetails;
import org.openbaton.catalogue.mano.common.monitoring.ThresholdType;
import org.openbaton.catalogue.nfvo.EndpointType;
import org.openbaton.catalogue.nfvo.Item;
import org.openbaton.exceptions.MonitoringException;
import org.openbaton.monitoring.interfaces.MonitoringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

public class PrometheusMonitoringAgent extends MonitoringPlugin {
	
	private int historyLength;
	private int requestFrequency;
	private String prometheusPluginIp;
	private PrometheusSender prometheusSender;
	private String notificationReceiverServerContext;
	private int notificationReceiverServerPort;
	private Gson mapper;
	private Random random = new Random();
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private List<AlarmEndpoint> subscriptions;
	//private Map<String, PmJob> pmJobs;
	//private Map<String, Threshold> thresholds;
	private Map<String, List<Alarm>> datacenterAlarms;
	private String type;
	private Map<String, List<String>> triggerIdHostnames;
	private Map<String, String> triggerIdActionIdMap;
	//private LimitedQueue<State> history;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	  //Server properties
	private HttpServer server;	
	//private MyHandler myHandler;

	public PrometheusMonitoringAgent(){
		loadProperties();
		String prometheusHost = properties.getProperty("prometheus-host", "192.168.56.3");
		String prometheusPort = properties.getProperty("prometheus-port", "9090");
		String username = properties.getProperty("user-zbx");
		String password = properties.getProperty("password-zbx");
		prometheusPluginIp = properties.getProperty("prometheus-plugin-ip");
		String prometheusEndpoint = properties.getProperty("zabbix-endpoint", "/api/v1/query?query=");
		prometheusSender = new PrometheusSender(prometheusHost,
				prometheusPort,
				prometheusEndpoint,
				false,
				username,
				password);
	}
	
	@Override
	public String subscribeForFault(AlarmEndpoint filter) throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String unsubscribeForFault(String alarmEndpointId) throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyFault(AlarmEndpoint endpoint, AbstractVirtualizedResourceAlarm event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Alarm> getAlarmList(String vnfId, PerceivedSeverity perceivedSeverity) throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createPMJob(ObjectSelection resourceSelector, List<String> performanceMetric,
			List<String> performanceMetricGroup, Integer collectionPeriod, Integer reportingPeriod)
			throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> deletePMJob(List<String> itemIdsToDelete) throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> queryPMJob(List<String> hostnames, List<String> metrics, String period)
			throws MonitoringException {
		List<Item> result = new ArrayList<>();
		for(String metric : metrics){
			try {
				JsonObject jsonObject = prometheusSender.callGet(metric);
				log.info(String.valueOf(jsonObject));
				JsonArray ms = jsonObject.get("data").getAsJsonObject().get("result").getAsJsonArray();

				for(JsonElement m : ms){
					String host = m.getAsJsonObject().get("metric").getAsJsonObject().get("instance").getAsString();
					if(hostnames.contains(host)){
						Item instance = new Item();
						instance.setMetric(metric);
						instance.setHostname(host);
						String value = m.getAsJsonObject().get("value").getAsJsonArray().get(1)
								.getAsString();
						instance.setValue(value);

						result.add(instance);
					}
				}
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public void subscribe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String createThreshold(ObjectSelection objectSelector, String performanceMetric, ThresholdType thresholdType,
			ThresholdDetails thresholdDetails) throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> deleteThreshold(List<String> thresholdIds) throws MonitoringException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queryThreshold(String queryFilter) {
		// TODO Auto-generated method stub
		
	}

}