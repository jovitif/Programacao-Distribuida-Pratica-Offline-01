package LojaDeCarros;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import entity.Carro;
import entity.Categorias;

public interface CarrosInterface extends Remote{
	public int adicionarCarro(String renavam, String modelo, int ano, double preco, int categoria) throws RemoteException;
	public Boolean removerCarro(String renavam)throws RemoteException;
	public Boolean alterarCarro(String renavam, String modelo, int ano, double preco, int categoria)throws RemoteException;
	public Boolean comprarCarro(Carro carro)throws RemoteException;
	
	
	public ArrayList<Carro> listarCarros()throws RemoteException;
	public Carro pesquisarCarro(String RenavanOuNome)throws RemoteException;
	public int getQuantidade()throws RemoteException;
}