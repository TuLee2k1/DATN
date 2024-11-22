package poly.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name= "pay")
public class Pay extends AbstractEntity{
    @Column(name = "amount")
    private Float amount;

    @Temporal(TemporalType.DATE)
    @Column(name = "paymentDate")
    private Date paymentDate;

    @Column(name = "paymentType")
    private String paymentType;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company_id;
}


