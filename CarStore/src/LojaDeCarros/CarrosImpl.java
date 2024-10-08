package LojaDeCarros;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import LojaDeCarrosBaseDados.BaseDadosInterface;
import entity.Carro;
import entity.Categorias;

public class CarrosImpl implements CarrosInterface{
	Map<String, Carro> carros;
    BaseDadosInterface baseDados;
    
	public CarrosImpl(int portBaseDados) {
		carros = new HashMap<String, Carro>();
		carros.put("23456789012", new Carro("23456789012", "Chevrolet Onix", 2019, 55000, Categorias.economico));
        carros.put("34567890123", new Carro("34567890123", "Ford Ka", 2020, 60000, Categorias.economico));
        carros.put("45678901234", new Carro("45678901234", "Hyundai HB20", 2017, 58000, Categorias.economico));
        carros.put("56789012345", new Carro("56789012345", "Nissan March", 2016, 52000, Categorias.economico));
        carros.put("67890123456", new Carro("67890123456", "Ford Ka Sedan", 2019, 72000, Categorias.intermediario));
        carros.put("78901234567", new Carro("78901234567", "Chevrolet Onix Plus", 2021, 78000, Categorias.intermediario));
        carros.put("89012345678", new Carro("89012345678", "Hyundai HB20S", 2020, 75000, Categorias.intermediario));
        carros.put("90123456789", new Carro("90123456789", "Renault Logan", 2018, 70000, Categorias.intermediario));
        carros.put("01234567890", new Carro("01234567890", "Toyota Etios", 2019, 68000, Categorias.intermediario));
        carros.put("09876543210", new Carro("09876543210", "Toyota Corolla", 2022, 120000, Categorias.executivo));
        carros.put("98765432109", new Carro("98765432109", "Honda Civic", 2021, 110000, Categorias.executivo));
        carros.put("87654321098", new Carro("87654321098", "Chevrolet Cruze", 2020, 105000, Categorias.executivo));
        carros.put("76543210987", new Carro("76543210987", "Audi A3", 2019, 125000, Categorias.executivo));
        
        conectarBaseDados(portBaseDados);
     
       
	}
	
	public void conectarBaseDados(int port) {
		try {
			Registry registro = LocateRegistry.getRegistry(port);
			baseDados = (BaseDadosInterface) registro.lookup("BaseDados");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public int adicionarCarro(String renavam, String modelo, int ano, double preco, int categoria) throws RemoteException {
		Categorias escolha = null;
		switch(categoria) {
		case 1:
			escolha  = Categorias.economico;
			break;
		case 2:
			escolha = Categorias.intermediario;
			break;
		case 3:
			escolha = Categorias.executivo;
			break;
		}
		Carro carro = new Carro(renavam, modelo, ano, preco, escolha);
	    
	    // Usando a interface funcional com uma expressão lambda para verificar e adicionar o carro
	    OperacaoCarro operacao = (Carro c) -> {
	        if (carros.containsKey(c.getRenavam())) {
	            return false; // Carro já existe
	        }
	        return baseDados.addCarro(c); // Adiciona o carro à base de dados
	    };

	    boolean sucesso = operacao.executar(carro);
	    if (sucesso) {
	        System.out.println("Carro adicionado");
	        return 2;
	    }
	    return 3; // Falha ao adicionar
	}

	@Override
	public Boolean removerCarro(String renavam) throws RemoteException {
	    OperacaoCarro operacao = (Carro c) -> baseDados.removeCarro(renavam);
	    
	    boolean removido = operacao.executar(null);
	    if (removido) {
	        System.out.println("Carro com renavam " + renavam + " removido");
	        return true;
	    }
	    return false;
	}

	@Override
	public ArrayList<Carro> listarCarros()throws RemoteException {
		System.out.println("retornando lista de carros...");
		return baseDados.listarCarros();
	}
	@Override
	public Carro pesquisarCarro(String renavamOuNome) throws RemoteException {
	    OperacaoCarro pesquisa = (Carro c) -> baseDados.pesquisarCarro(renavamOuNome) != null;
	    
	    Carro carro = baseDados.pesquisarCarro(renavamOuNome);
	    if (pesquisa.executar(carro)) {
	        System.out.println("Carro encontrado, retornando para o usuário");
	        return carro;
	    }
	    System.out.println("Carro não encontrado na base de dados");
	    return null;
	}

	@Override
	public Boolean alterarCarro(String renavam, String modelo, int ano, double preco, int categoria)throws RemoteException{
		Categorias escolha = null;
		switch(categoria) {
		case 1:
			escolha  = Categorias.economico;
			break;
		case 2:
			escolha = Categorias.intermediario;
			break;
		case 3:
			escolha = Categorias.executivo;
			break;
		}
		boolean edit = baseDados.alterarCarro(renavam, modelo, ano, preco, escolha);
		if(edit) {
			System.out.println("carro editado na base de dados");
			return edit;
		}
		System.out.println("falha ao editar carro na base de dados");
		return false;
	}
	@Override
	public int getQuantidade() throws RemoteException{
		return baseDados.getQuantidade();
	}
	@Override
	public Boolean comprarCarro(Carro carro)throws RemoteException {
		
		return baseDados.comprarCarro(carro);
	}
}
