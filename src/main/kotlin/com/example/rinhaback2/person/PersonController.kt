package com.example.rinhaback2.person

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
class PersonController(private val repository: PersonRepository,
                       private val redisTemplate: RedisTemplate<String, Person>) {

    @PostMapping("/pessoas")
    fun save(@RequestBody person: Person,
             ucb: UriComponentsBuilder) : ResponseEntity<Void> {

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


}