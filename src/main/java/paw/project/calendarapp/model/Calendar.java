package paw.project.calendarapp.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Component
@Data
public class Calendar {

    //Pola
    private LocalDate currentDate = LocalDate.now().withDayOfMonth(1); // Aktualna data (ustawiona na 1 dzień miesiąca)
    private int month = currentDate.getMonthValue(); // Aktualny miesiąc
    private int daysInMonth = currentDate.lengthOfMonth(); // Ilość dni aktualnego miesiąca
    private int year = currentDate.getYear(); // Aktualny rok
    private int dayOfWeek = currentDate.getDayOfWeek().getValue(); // Dzień tygodnia pierwszego dnia miesiąca
    private List<Note> notes; // Lista notek użytkownika
    private final MessageSource messageSource; //Źródło plików messages
    private MessageSourceAccessor accessor; //Dostęp do plików messages
    private String monthName; // Nazwa aktualnego miesiąca
    private Locale locale; //Ustawienia języka

    //Wstrzykiwanie MessageSource
    @Autowired
    public Calendar(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = LocaleContextHolder.getLocale();
        this.accessor = new MessageSourceAccessor(messageSource);
        this.monthName = monthName(month);
    }

    //Metody
    //Następny miesiąc
    public void incrementMonth(){
        currentDate = currentDate.plusMonths(1);
        daysInMonth = currentDate.lengthOfMonth();
        year = currentDate.getYear();
        month = currentDate.getMonthValue();
        monthName = monthName(month);
        dayOfWeek = currentDate.getDayOfWeek().getValue();
    }

    //Poprzedni miesiąc
    public void decrementMonth(){
        currentDate = currentDate.minusMonths(1);
        daysInMonth = currentDate.lengthOfMonth();
        year = currentDate.getYear();
        month = currentDate.getMonthValue();
        monthName = monthName(month);
        dayOfWeek = currentDate.getDayOfWeek().getValue();
    }

    //Ustal nazwę miesiąca
    public String monthName(int month){
        String monthName = null;
        switch (month) {
            case 1:
                monthName = accessor.getMessage("lang.january",locale);
                break;
            case 2:
                monthName = accessor.getMessage("lang.february",locale);
                break;
            case 3:
                monthName = accessor.getMessage("lang.march",locale);
                break;
            case 4:
                monthName = accessor.getMessage("lang.april",locale);
                break;
            case 5:
                monthName = accessor.getMessage("lang.may",locale);
                break;
            case 6:
                monthName = accessor.getMessage("lang.june",locale);
                break;
            case 7:
                monthName = accessor.getMessage("lang.july",locale);
                break;
            case 8:
                monthName = accessor.getMessage("lang.august",locale);
                break;
            case 9:
                monthName = accessor.getMessage("lang.september",locale);
                break;
            case 10:
                monthName = accessor.getMessage("lang.october",locale);
                break;
            case 11:
                monthName = accessor.getMessage("lang.november",locale);
                break;
            case 12:
                monthName = accessor.getMessage("lang.december",locale);
                break;
        }
        return monthName;
    }

    public void setLocale(Locale locale){
        this.locale = locale;
        monthName = monthName(month);
    }

}
