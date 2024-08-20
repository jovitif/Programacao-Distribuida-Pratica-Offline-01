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