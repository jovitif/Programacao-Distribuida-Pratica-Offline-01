package gateway;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import LojaDeCarros.CarrosInterface;
import autenticacao.AutenticacaoInterface;

public class GatewayImpl implements GatewayInterface {
	public int n = 0;
	private int[] conexoesPorReplica = {0,0,0};
	
    private ServidorSelector servidorLoja = () -> getServidorLoja();
    private ServidorSelector servidorReplica01 = () -> getServidorLojaReplica01();
    private ServidorSelector servidorReplica02 = () -> getServidorLojaReplica02();

    private ServidorSelector selectByLeastConnections(int[] pesos) {
    	int[] pesosDasReplicas = pesos;
    	if(conexoesPorReplica[0]*pesosDasReplicas[0] <= conexoesPorReplica[1]*pesosDasReplicas[1]) {
    		if(conexoesPorReplica[0]*pesosDasReplicas[0]<=conexoesPorReplica[2]*pesosDasReplicas[2]) {
    			
    			conexoesPorReplica[0]++;
    			
    			for(int i = 0; i<conexoesPorReplica.length; i++) {
    				System.out.println("conexões replica "+(i+1)+": "+conexoesPorReplica[i]*pesosDasReplicas[i]);
    			}
    			
        		return servidorLoja;
    		}
    		else {
    			conexoesPorReplica[2]++;
    			
    			for(int i = 0; i<conexoesPorReplica.length; i++) {
    				System.out.println("conexões replica "+(i+1)+": "+conexoesPorReplica[i]*pesosDasReplicas[i]);
    			}
    			
    			return servidorReplica02;
    		}
    		
    	}
    	else if(conexoesPorReplica[1]*pesosDasReplicas[1] <= conexoesPorReplica[0]*pesosDasReplicas[0]) {
    		if(conexoesPorReplica[1]*pesosDasReplicas[1]<=conexoesPorReplica[2]*pesosDasReplicas[2]) {
    			
    			conexoesPorReplica[1]++;
    			
    			for(int i = 0; i<conexoesPorReplica.length; i++) {
    				System.out.println("conexões replica "+(i+1)+": "+conexoesPorReplica[i]*pesosDasReplicas[i]);
    			}
    			
    			return servidorReplica01;
    		}
    		else {
    			conexoesPorReplica[2]++;
    			
    			for(int i = 0; i<conexoesPorReplica.length; i++) {
    				System.out.println("conexões replica "+(i+1)+": "+conexoesPorReplica[i]*pesosDasReplicas[i]);
    			}
    			
    			return servidorReplica02;
    		}
    	}
    	else if(conexoesPorReplica[2]*pesosDasReplicas[2] <= conexoesPorReplica[0]*pesosDasReplicas[0]) {
    		if(conexoesPorReplica[2]*pesosDasReplicas[2] <=conexoesPorReplica[1]*pesosDasReplicas[1]) {
    			
    			conexoesPorReplica[2]++;
    			
    			for(int i = 0; i<conexoesPorReplica.length; i++) {
    				System.out.println("conexões replica "+(i+1)+": "+conexoesPorReplica[i]*pesosDasReplicas[i]);
    			}
    			
    			return servidorReplica02;
    		}
    		else {
    			conexoesPorReplica[1]++;
    			
    			for(int i = 0; i<conexoesPorReplica.length; i++) {
    				System.out.println("conexões replica "+(i+1)+": "+conexoesPorReplica[i]*pesosDasReplicas[i]);
    			}
    			
    			return servidorReplica01;
    		}
    		
    	}
    	else if (conexoesPorReplica[0]*pesosDasReplicas[0] == conexoesPorReplica[1]*pesosDasReplicas[1] &&
    			 conexoesPorReplica[0]*pesosDasReplicas[0] == conexoesPorReplica[2]*pesosDasReplicas[2] &&
    		     conexoesPorReplica[1]*pesosDasReplicas[1] == conexoesPorReplica[2]*pesosDasReplicas[2]) {
    		
    		conexoesPorReplica[0]++;
    		
    		for(int i = 0; i<conexoesPorReplica.length; i++) {
				System.out.println("conexões: "+conexoesPorReplica[i]*pesosDasReplicas[i]);
			}
    		
    		return servidorLoja;
    	}
    	
    	return null;
    }
	
	@Override
	public AutenticacaoInterface getServidorAutenticacao() {
		try {
			Registry registro = LocateRegistry.getRegistry(1100);
			AutenticacaoInterface servidorAuth = (AutenticacaoInterface) registro.lookup("Autenticacao");
			System.out.println("retornando servidor de autenticação para cliente...");
			return servidorAuth;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CarrosInterface getServidorLoja() {
		try {
			Registry registro = LocateRegistry.getRegistry(1101);
			CarrosInterface servidorLoja = (CarrosInterface) registro.lookup("Carros");
			System.out.println("retornando servidor da loja principal para cliente...");
			return servidorLoja;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public CarrosInterface getServidorLojaReplica01() {
		try {
			Registry registro = LocateRegistry.getRegistry(1102);
			CarrosInterface servidorLoja = (CarrosInterface) registro.lookup("Carros");
			System.out.println("retornando replica01 da loja para cliente...");
			return servidorLoja;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public CarrosInterface getServidorLojaReplica02() {
		try {
			Registry registro = LocateRegistry.getRegistry(1103);
			CarrosInterface servidorLoja = (CarrosInterface) registro.lookup("Carros");
			System.out.println("retornando replica02 da loja para cliente...");
			return servidorLoja;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	 public CarrosInterface selectReplica(int tipoSelecao) {

	        ServidorSelector selector = null;
	        // Metodo Round Robin
	        if(tipoSelecao == 0) {
	        	System.out.println("Método Round Robin:");
	        	System.out.println("Valor n = " + n);
	        	switch (n) {
	            case 0:
	                selector = servidorLoja;
	                n = 1;
	                break;
	            case 1:
	                selector = servidorReplica01;
	                n = 2;
	                break;
	            case 2:
	                selector = servidorReplica02;
	                n = 0;
	                break;
	        	}
		 	}
	        // Metodo Least Connections
	        else if(tipoSelecao == 1) {
	        	System.out.println("Método Least Connections:");
	        	int[] pesos = {1,1,1};
	        	selector = selectByLeastConnections(pesos);
	        }
	        else if(tipoSelecao == 2) {
	        	System.out.println("Método Weighted Least Connections:");
	        	int[] pesos = {1,3,2};
	        	selector = selectByLeastConnections(pesos);
	        }
	        
	        try {
	            return selector != null ? selector.selecionarServidor() : null;
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	}
