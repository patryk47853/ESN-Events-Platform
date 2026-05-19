package com.esn.ticket.event;
   
   import lombok.AllArgsConstructor;
   import lombok.Builder;
   import lombok.Data;
   import lombok.NoArgsConstructor;
   
   @Data
   @Builder
   @NoArgsConstructor
   @AllArgsConstructor
   public class TicketCancelledEvent {
       private Long ticketId;
       private Long eventId;
       private Long userId;
       private String reason;
   }