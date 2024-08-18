package LojaDeCarrosBaseDados;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import entity.Carro;
import entity.Categorias;

public class BaseDadosImpl implements BaseDadosInterface {
    
    Map<String, Carro> carros;
    boolean emProcesso = false;
    
    BaseDadosInterface proxBaseDados;
    
    public BaseDadosImpl() {
        carros = new HashMap<>();
        carros.put("23456789012", new Carro("23456789012", "Chevrolet Onix", 2019, 55000, Categorias.economico));
        // ... (Outros carros)
    }
    
    public void connectNext(int port) {
        try {
            Thread.sleep(10000);
            Registry registro = LocateRegistry.getRegistry(port);
            proxBaseDados = (BaseDadosInterface) registro.lookup("BaseDados");
            System.out.println("conectado na proxima base de dados na porta: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean permissao(int cont) {
        if (cont > 2) {
            System.out.println("permissao para alterar base de dados concedida");
            return true;
        }
        if (emProcesso) {
            return false;
        }
        try {
            return proxBaseDados.permissao(cont + 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public void atualizarBaseDados(Map<String, Carro> carros, int cont) {
        if (cont != 0 && cont < 3) {
            System.out.println("atualizando base de dados");
            this.carros = carros;
            try {
                proxBaseDados.atualizarBaseDados(carros, cont + 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            if (cont == 3) {
                System.out.println("todas as bases atualizadas");
            } else {
                try {
                    proxBaseDados.atualizarBaseDados(carros, cont + 1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public boolean addCarro(Carro carro) {
        return realizarAcaoComPermissao(() -> {
            carros.put(carro.getRenavam(), carro);
            atualizarBaseDados(carros, 0);
            return true;
        });
    }
    
    @Override
    public Boolean removeCarro(String renavam) throws RemoteException {
        return realizarAcaoComPermissao(() -> {
            Carro carro = carros.remove(renavam);
            atualizarBaseDados(carros, 0);
            return carro != null;
        });
    }
    
    @Override
    public ArrayList<Carro> listarCarros() throws RemoteException {
        return carros.values().stream()
            .sorted(Comparator.comparing(Carro::getNome))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    @Override
    public Carro pesquisarCarro(String renavamOuNome) throws RemoteException {
        return carros.values().stream()
            .filter(carro -> carro.getRenavam().equalsIgnoreCase(renavamOuNome)
                           || carro.getNome().equalsIgnoreCase(renavamOuNome))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public Boolean alterarCarro(String renavam, String modelo, int ano, double preco, Categorias categoria) throws RemoteException {
        return realizarAcaoComPermissao(() -> {
            try {
                Carro carro = pesquisarCarro(renavam);
                if (carro != null) {
                    carro.setNome(modelo);
                    carro.setAnoFabricacao(ano);
                    carro.setPreco(preco);
                    carro.setCategoria(categoria);
                    atualizarBaseDados(carros, 0);
                    return true;
                }
                return false;
            } catch (RemoteException e) {
                e.printStackTrace();  // Ou qualquer outro tratamento de erro que você preferir
                return false;
            }
        });
    }

    
    @Override
    public int getQuantidade() throws RemoteException {
        return carros.size();
    }
    
    @Override
    public Boolean comprarCarro(Carro carro) throws RemoteException {
        return realizarAcaoComPermissao(() -> {
            boolean carroRemovido = carros.remove(carro.getRenavam(), carro);
            atualizarBaseDados(carros, 0);
            return carroRemovido;
        });
    }
    
    // Método auxiliar para realizar uma ação com permissão
    private Boolean realizarAcaoComPermissao(AcaoComPermissao acao) {
        if (permissao(0)) {
            emProcesso = true;
            boolean resultado = acao.executar();
            emProcesso = false;
            return resultado;
        } else {
            System.out.println("permissao negada");
            return false;
        }
    }
    
    // Interface funcional para ação com permissão
    @FunctionalInterface
    interface AcaoComPermissao {
        boolean executar();
    }
}