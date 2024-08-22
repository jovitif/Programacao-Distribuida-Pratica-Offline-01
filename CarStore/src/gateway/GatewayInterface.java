 package gateway;

import java.rmi.Remote;
import java.rmi.RemoteException;

import LojaDeCarros.CarrosInterface;
import autenticacao.AutenticacaoInterface;


public interface GatewayInterface extends Remote {
    AutenticacaoInterface getServidorAutenticacao() throws RemoteException;
    CarrosInterface getServidorLoja() throws RemoteException;
    CarrosInterface getServidorLojaReplica01() throws RemoteException;
    CarrosInterface getServidorLojaReplica02() throws RemoteException;
    CarrosInterface selectReplica(int tipoSelecao) throws RemoteException;
}
