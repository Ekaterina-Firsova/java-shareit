package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mvc;

    @Mock
    private BookingServiceImpl bookingService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper.registerModule(new JavaTimeModule()); //для корректного разбора LocalDateTime
    }

    @Test
    public void testGetBookingsByState() throws Exception {
        long userId = 1L;
        String state = "APPROVED";
        List<BookingDto> bookingsDto = List.of(
                Instancio.of(BookingDto.class).create(),
                Instancio.of(BookingDto.class).create());

        UserDto userDto = Instancio.of(UserDto.class)
                .generate(field("email"), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
                .create();

        when(userService.getById(userId)).thenReturn(userDto);
        when(bookingService.getBookingsByState(userId, state)).thenReturn(bookingsDto);

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingsDto)));

        Mockito.verify(userService).getById(userId);
        Mockito.verify(bookingService).getBookingsByState(userId, state);
    }

    @Test
    public void testCreateBooking() throws Exception {
        long userId = 1L;

        BookingDto bookingDto = Instancio.of(BookingDto.class)
                .ignore(field("start"))
                .ignore(field("end"))
                .create();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingService.create(userId, bookingDto)).thenReturn(bookingDto);

        String bookingDtoJson = objectMapper.writeValueAsString(bookingDto);

        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingDtoJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService).create(userId, bookingDto);
    }

    @Test
    public void testUpdateBooking() throws Exception {
        long userId = 1L;
        Boolean approved = true;
        BookingDto bookingDto = Instancio.of(BookingDto.class)
                .create();

        Mockito.when(bookingService.update(Mockito.eq(userId), Mockito.eq(bookingDto.getId()), Mockito.eq(approved)))
                .thenReturn(bookingDto);

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService).update(Mockito.eq(userId), Mockito.eq(bookingDto.getId()), Mockito.eq(approved));
    }

    @Test
    public void testGetBookingOwner() throws Exception {
        long userId = 1L;
        String state = "CURRENT";
        List<BookingDto> bookingsDto = List.of(
                Instancio.of(BookingDto.class).create(),
                Instancio.of(BookingDto.class).create());

        Mockito.when(bookingService.getBookingOwner(state, userId))
                .thenReturn(bookingsDto);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingsDto)));

        Mockito.verify(bookingService).getBookingOwner(state, userId);
    }

}