package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start asc " +
            "limit 1")
    Booking findNextBooking(long itemId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc " +
            "limit 1")
    Booking findLastBooking(long itemId, LocalDateTime now);

    List<Booking> findAllByBooker_Id(long userId, Sort start);

    List<Booking> findAllByBooker_IdAndEndIsAfterAndStartIsBefore(long userId, LocalDateTime now1, LocalDateTime now2, Sort start);

    List<Booking> findAllByBooker_IdAndStartIsAfter(long userId, LocalDateTime now, Sort start);

    List<Booking> findAllByBooker_IdAndEndIsBefore(long userId, LocalDateTime now, Sort start);

    List<Booking> findAllByBooker_IdAndStatus(long userId, BookingStatus bookingStatus, Sort start);

    List<Booking> findAllByItem_Owner_Id(long userId, Sort start);

    List<Booking> findAllByItem_Owner_IdAndEndIsAfterAndStartIsBefore(long userId, LocalDateTime now1, LocalDateTime now2, Sort start);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(long userId, LocalDateTime now, Sort start);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(long userId, LocalDateTime now, Sort start);

    List<Booking> findAllByItem_Owner_IdAndStatus(long userId, BookingStatus bookingStatus, Sort start);

    boolean existsByItem_IdAndBooker_IdAndEndIsBefore(Long itemId, Long bookerId, LocalDateTime now);

}
