package ru.practicum.shareit.request;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {


    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto requestDto,
                             @RequestHeader(Constants.USER_ID) long requesterId) {
        return requestService.create(requestDto, requesterId);
    }


    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long userId) {
        return requestService.findById(id, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(Constants.USER_ID) long requesterId) {
        return requestService.findAllFromUser(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsForAnswer(@RequestHeader(Constants.USER_ID) long userId,
                                                        @RequestParam (defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        return requestService.findAllRequestsForAnswer(userId, from, size);
    }


}
