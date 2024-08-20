package LojaDeCarros;

import java.rmi.RemoteException;

import entity.Carro;

@FunctionalInterface
interface OperacaoCarro {
    boolean executar(Carro carro) throws RemoteException;
}
