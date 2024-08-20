package autenticacao;

import entity.Usuario;

@FunctionalInterface
public interface VerificacaoAutenticacao {
    boolean verificar(Usuario u, Usuario user);
}
