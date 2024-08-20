package gateway;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import LojaDeCarros.CarrosInterface;
import autenticacao.AutenticacaoInterface;

public class GatewayImpl implements GatewayInterface {
	public int n = 0;
	
    private ServidorSelector servidorLoja = () -> getServidorLoja();
    private ServidorSelector servidorReplica01 = () -> getServidorLojaReplica01();
    private ServidorSelector servidorReplica02 = () -> getServidorLojaReplica02();

	
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
	
	 public CarrosInterface selectReplica() {
	        System.out.println("Valor n = " + n);

	        ServidorSelector selector = null;
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

	        try {
	            return selector != null ? selector.selecionarServidor() : null;
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	}
