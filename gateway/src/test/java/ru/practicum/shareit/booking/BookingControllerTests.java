package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    public void testGetBookingsByState() throws Exception {
        long userId = 1L;
        String state = String.valueOf(BookingStatus.APPROVED);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("mocked response");

        when(bookingClient.getBookingsByState(userId, BookingStatus.valueOf(state))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("mocked response"));

        Mockito.verify(bookingClient).getBookingsByState(userId, BookingStatus.valueOf(state));
    }

    @Test
    public void testGetBookingsByStateIllegal() throws Exception {
        long userId = 1L;
        String state = "qwer";
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateBooking() throws Exception {
        long userId = 1L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Booking created");

        Mockito.when(bookingClient.create(Mockito.eq(userId), Mockito.any(BookingDto.class)))
                .thenReturn(expectedResponse);

        String bookingDtoJson = mapper.writeValueAsString(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingDtoJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Booking created"));

        Mockito.verify(bookingClient).create(Mockito.eq(userId), Mockito.any(BookingDto.class));
    }

    @Test
    public void testUpdateBooking() throws Exception {
        long userId = 1L;
        long bookingId = 10L;
        Boolean approved = true;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Booking updated");

        Mockito.when(bookingClient.update(Mockito.eq(userId), Mockito.eq(bookingId), Mockito.eq(approved)))
                .thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Booking updated"));

        Mockito.verify(bookingClient).update(Mockito.eq(userId), Mockito.eq(bookingId), Mockito.eq(approved));
    }

    @Test
    public void testGetBookingOwner() throws Exception {
        long userId = 1L;
        String state = "ALL";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Owner's bookings list");

        Mockito.when(bookingClient.getBookingOwner(Mockito.eq(userId), Mockito.eq(state)))
                .thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Owner's bookings list"));

        Mockito.verify(bookingClient).getBookingOwner(Mockito.eq(userId), Mockito.eq(state));
    }
}


