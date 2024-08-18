package autenticacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import entity.TipoUsuario;
import entity.Usuario;

public class AutenticacaoImpl implements AutenticacaoInterface {
    private List<Usuario> usuarios; 
    
    public AutenticacaoImpl() {
        usuarios = new ArrayList<>();
        usuarios.add(new Usuario("funcionario1", "senha123", TipoUsuario.funcionario));
        usuarios.add(new Usuario("cliente1", "senha321", TipoUsuario.cliente));
    }
    
    @Override
    public Usuario autenticarUsuario(Usuario user) {
        // Interface funcional para verificação de autenticação
        VerificacaoAutenticacao verificacao = (u, usuario) -> 
                u.getLogin().equals(usuario.getLogin()) && 
                u.getSenha().equals(usuario.getSenha());
        
        // Uso de expressão lambda para encontrar o usuário autenticado
        Optional<Usuario> usuarioAutenticado = usuarios.stream()
            .filter(u -> verificacao.verificar(u, user))
            .findFirst();
        
        // Interface funcional para processar o usuário autenticado
        AcaoUsuarioAutenticado acaoAutenticado = u -> 
            System.out.println("Usuario: " + u.getLogin() + " autenticado");
        
        // Processando o usuário autenticado, se presente
        usuarioAutenticado.ifPresent(acaoAutenticado::processar);
        
        return usuarioAutenticado.orElse(null);
    }

    // Método para filtrar usuários com base em uma condição usando uma interface funcional
    public List<Usuario> filtrarUsuarios(FiltroUsuario filtro) {
        return usuarios.stream()
            .filter(filtro::filtrar)
            .collect(Collectors.toList());
    }
    
    // Exemplo de uso da nova interface funcional de filtro
    public void exemploFiltrarPorTipo(TipoUsuario tipo) {
        FiltroUsuario filtroPorTipo = usuario -> usuario.getTipo().equals(tipo);
        List<Usuario> usuariosFiltrados = filtrarUsuarios(filtroPorTipo);
        
        usuariosFiltrados.forEach(u -> 
            System.out.println("Usuario do tipo " + tipo + ": " + u.getLogin()));
    }
}
