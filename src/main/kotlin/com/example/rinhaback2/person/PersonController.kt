package com.example.rinhaback2.person

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@RestController
class PersonController(private val repository: PersonRepository,
                       private val redisTemplate: RedisTemplate<String, Person>) {

    @PostMapping("/pessoas")
    fun save(@RequestBody person: Person,
             ucb: UriComponentsBuilder) : ResponseEntity<Void> {

        if (person.nome.length > 100 || person.apelido.length > 32) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }

        val addedNickname = redisTemplate.opsForValue().get(person.apelido)


        if (addedNickname != null){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }


        val id = repository.save(person)

        val p = Person(
            id = id,
            nome = person.nome,
            apelido = person.apelido,
            nascimento =  person.nascimento,
            stack = person.stack
        )

        redisTemplate.opsForValue().set(id, p)
        redisTemplate.opsForValue().set(p.apelido, p)

        val uri = ucb.path("/pessoas/{id}").buildAndExpand(id).toUri()

        return ResponseEntity.created(uri).build()

    }

    @GetMapping("/pessoas/{id}")
    fun getById(@PathVariable id: String) : ResponseEntity<Person> {

        val cachedPerson = redisTemplate.opsForValue().get(id)

        if (cachedPerson != null){
            println("returned by cache")
            return ResponseEntity.ok().body(cachedPerson)
        }

        return ResponseEntity.ok().body(repository.findById(UUID.fromString(id)))
    }

    @GetMapping("/pessoas")
    fun getByCriteria(@RequestParam("t") criteria: String) : ResponseEntity<List<Person>>{
        return try {
            ResponseEntity.ok().body(repository.findByCriteria(criteria))
        } catch (ex: Exception){
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/contagem-pessoas")
    fun getStoredPeople() : Long {
        return repository.count();
    }

    @GetMapping("/err")
    fun getErr() : ResponseEntity<MutableList<String>> {
        return ResponseEntity.ok(PersonExceptionHandler.listErr)
    }


}