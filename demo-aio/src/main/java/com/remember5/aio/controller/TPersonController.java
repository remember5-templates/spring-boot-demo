package com.remember5.aio.controller;

import com.remember5.aio.domain.TPerson;
import com.remember5.aio.service.TPersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* (t_person)表控制层
*
* @author xxxxx
*/
@Tag(name = "person controller")
@RestController
@RequestMapping("/t_person")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TPersonController {

    private final TPersonService tPersonService;

    @GetMapping("/:id")
    @Operation(summary = "get by id")
    public TPerson getById(Long id){
        return tPersonService.getById(id);
    }

}
