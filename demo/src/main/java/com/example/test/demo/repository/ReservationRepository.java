package com.example.test.demo.repository;

import com.example.test.demo.dto.ParkingLotStatisticsDTO;
import com.example.test.demo.entity.Reservation;
import com.example.test.demo.entity.ReservationStatus;
import com.example.test.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 중복 예약 방지용 (해당 자리, 시간 겹치는 예약)
    @Query(
            "SELECT r FROM Reservation r WHERE r.parkingSlot.id = :slotId AND r.status = 'ACTIVE' "
            + "AND (r.startTime < :endTime AND r.endTime > :startTime)"
    )
    List<Reservation> findConflictsBySlot(
            @Param("slotId") Long slotId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.parkingSlot.parkingLot.id = :parkingLotId
          AND r.status IN ('RESERVED', 'ACTIVE')
          AND r.endTime > :start
          AND r.startTime < :end
    """)
    List<Reservation> findConflictsByParkingLot(
            @Param("parkingLotId") Long parkingLotId,
            @Param("start") LocalDateTime startTime,
            @Param("end") LocalDateTime endTime
    );

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserAndStatus(User user, ReservationStatus status);

    List<Reservation> findByParkingSlot_ParkingLot_Id(Long spaceId);

    List<Reservation> findByStatusAndEndTimeBefore(ReservationStatus status, LocalDateTime endTime);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.user.id = :userId
            AND r.parkingSlot.id = :slotId
            AND r.status = 'ACTIVE'
            """)
    Optional<Reservation> findActiveReservation(Long userId, Long slotId, LocalDateTime now);

    @Query("SELECT COUNT(r), COALESCE(SUM(r.totalPrice), 0) " +
            "FROM Reservation r " +
            "WHERE r.parkingSlot.parkingLot.id = :parkingLotId")
    List<Object[]> getReservationStatsByParkingLotId(@Param("parkingLotId") Long parkingLotId);

//    @Query("""
//    SELECT new com.example.test.demo.dto.ParkingLotStatisticsDTO(
//        r.parkingSlot.parkingLot.id,
//        r.parkingSlot.parkingLot.name,
//        COUNT(r),
//        COALESCE(SUM(r.totalPrice), 0)
//    )
//    FROM Reservation r
//    GROUP BY r.parkingSlot.parkingLot.id, r.parkingSlot.parkingLot.name
//            """)
//    List<ParkingSpaceStats> findParkingSpaceSummary();

    @Query(
            """
            SELECT new com.example.test.demo.dto.ParkingLotStatisticsDTO(
                ps.id,
                ps.name,
                COUNT(r),
                COALESCE(SUM(r.totalPrice), 0)
            )
            FROM Reservation r
            JOIN r.parkingSlot pslt
            JOIN pslt.parkingLot ps
            GROUP BY ps.id, ps.name
            """
    )
    List<ParkingLotStatisticsDTO> findAllStatisticsByParkingLot();

    @Query("""
        SELECT COUNT(r), COALESCE(SUM(r.totalPrice), 0)
        FROM Reservation r
        WHERE r.parkingSlot.parkingLot.id = :parkingLotId
        AND r.startTime BETWEEN :start AND :end
    """)
    List<Object[]> findStatsByDateRange(
            @Param("parkingLotId") Long parkingLotId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    ); // 날짜별 통계용

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.parkingSlot.parkingLot.id = :ownerId
    """)
    List<Reservation> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.parkingSlot.parkingLot.id = :ownerId
            AND r.status = :status
    """)
    List<Reservation> findByOwnerIdAndStatus(
            @Param("ownId") Long ownerId,
            @Param("status") ReservationStatus status
    );

    List<Reservation> findByStatus(ReservationStatus status);


}
