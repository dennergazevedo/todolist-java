package dev.dnnr.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dnnr.todolist.utils.Utils;
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

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request){
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);
    return tasks;
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel task, @PathVariable UUID id, HttpServletRequest request){
    var currentTask = this.taskRepository.findById(id).orElse(null);

    if(task == null){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Tarefa não encontrada");
    }

    var idUser = request.getAttribute("idUser");

    if(!currentTask.getIdUser().equals(idUser)){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Usuário não tem permissão para alterar essa tarefa");
    }

    Utils.copyNonNullProperties(task, currentTask);
    
    var taskUpdated = this.taskRepository.save(currentTask);
    return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
  }
}
