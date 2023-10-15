package dev.dnnr.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;
  
  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request){
    var idUser = request.getAttribute("idUser");
    task.setIdUser((UUID) idUser);

    var currentDate = LocalDateTime.now();
    if(currentDate.isBefore(task.getStartAt()) || currentDate.isAfter(task.getEndAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("A data do início/término deve ser maior que a data atual");
    }

    if(task.getStartAt().isAfter(task.getEndAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("A data de início deve ser menor que a data de término");
    }

    var response = this.taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
