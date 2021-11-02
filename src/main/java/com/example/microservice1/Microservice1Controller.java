package com.example.microservice1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Microservice1Controller {

private static final Logger logger = LoggerFactory.getLogger(Microservice1Controller.class);
	
	@Value("${serviceHost}")
	private String serviceHost;
	
	@Value("${mq.queue}")
    private String nombreCola;
	
	@Value("${ddbb.mysql}")
    private String ddbb;
	
	

	@GetMapping("/test/{user}")
	public String enviaRespuesta(@PathVariable String user) {
		logger.info("microservice1 consumido por "+user);
		logger.info("serviceHost: "+serviceHost);
		
		RestTemplate restTemplate = new RestTemplate();
		String testUrl = "http://" + serviceHost + ":8080/microservice2/enviaRespuesta/";
		
		//consume microservice2
		ResponseEntity<String> resp = restTemplate.getForEntity(testUrl + nombreCola, String.class);		
		return resp.getBody();
	}
	
	@GetMapping("/ddbb")
	public String ddbb() throws Exception {
		logger.info("default endpoint");
		Connection con= null;
		StringBuilder sb = new StringBuilder();
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
	
			logger.info("ddbb: "+ddbb);
	//		Connection con=DriverManager.getConnection("jdbc:mysql://db4free.net:3306/ddbb_reca1","guest_reca1","guest_reca1");
			con=DriverManager.getConnection("jdbc:mysql://"+ddbb+"/ddbb_reca1","guest_reca1","guest_reca1");
			//here sonoo is the database name, root is the username and root is the password
			Statement stmt=con.createStatement();
	
			ResultSet rs=stmt.executeQuery("SELECT ID, NOMBRE_USUARIO, FECHA_HORA, IP, NOMBRE_MICROSERVICIO, NOMBRE_PROCESO, DESCRIPCION, VALOR_ANTERIOR, VALOR_NUEVO\r\n"
					+ "FROM ddbb_reca1.AUDITORIA_FLUJOS;");
	
			while(rs.next()) {
				
				sb.append(rs.getString("NOMBRE_USUARIO"));
				sb.append(",");
				sb.append(rs.getString("NOMBRE_MICROSERVICIO"));
				sb.append(",");
				sb.append(rs.getString("NOMBRE_PROCESO"));
				sb.append(",");
				sb.append(rs.getString("DESCRIPCION"));
				
				
			}
		
		} finally {
			con.close();
		}
		

		
		return "ddbb response: "+sb.toString();
	}
	
	@GetMapping("/")
	public String defecto() {
		return "default API";
	}
	
}
