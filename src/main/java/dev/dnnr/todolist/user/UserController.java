package dev.dnnr.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private IUserRepository userRepository;
  
  @PostMapping("/")
  public ResponseEntity create(@RequestBody UserModel user){
    var existUser = this.userRepository.findByUsername(user.getUsername());

    if(existUser != null) {
      /* APENAS PARA TESTE, REGRA NÃO FAZ SENTIDO FALANDO DE LGPD */
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
    }

    /* HASH PASSWORD */
    var hash = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
    user.setPassword(hash);

    /* SALVANDO NA DATABASE */
    var userCreated = this.userRepository.save(user);

    /* RETORNO COM STATUS HTTP PERSONALIZADO */
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
  }
}
