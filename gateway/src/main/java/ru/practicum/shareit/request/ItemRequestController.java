package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody ItemRequestDto requestDto,
                                         @RequestHeader(Constants.USER_ID) long requesterId) {
        return requestClient.create(requestDto, requesterId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long userId) {
        return requestClient.findById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader(Constants.USER_ID) long requesterId) {
        return requestClient.findAllFromUser(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsForAnswer(@RequestHeader(Constants.USER_ID) long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        return requestClient.findAllRequestsForAnswer(userId, from, size);
    }

}
