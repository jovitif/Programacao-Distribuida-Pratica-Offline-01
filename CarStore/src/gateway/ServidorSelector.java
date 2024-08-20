package gateway;

import java.rmi.RemoteException;

import LojaDeCarros.CarrosInterface;

@FunctionalInterface
public interface ServidorSelector {
    CarrosInterface selecionarServidor() throws RemoteException;
}
