package paw.project.calendarapp.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import paw.project.calendarapp.model.Note;

import java.time.LocalDate;
import java.util.List;

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
    private LocaleHolder localeHolder; //Komponent przechowujący aktualne Locale aplikacji

    //Wstrzykiwanie MessageSource
    @Autowired
    public Calendar(MessageSource messageSource, LocaleHolder localeHolder) {
        this.messageSource = messageSource;
        this.localeHolder = localeHolder;
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

    //Ustaw miesiąc
    public void setMonth(int month){
        currentDate = currentDate.withMonth(month);
        daysInMonth = currentDate.lengthOfMonth();
        year = currentDate.getYear();
        this.month = currentDate.getMonthValue();
        monthName = monthName(this.month);
        dayOfWeek = currentDate.getDayOfWeek().getValue();
    }

    //Ustaw rok
    public void setYear(int year){
        currentDate = currentDate.withYear(year);
        daysInMonth = currentDate.lengthOfMonth();
        this.year = currentDate.getYear();
        month = currentDate.getMonthValue();
        monthName = monthName(month);
        dayOfWeek = currentDate.getDayOfWeek().getValue();
    }

    //Resetuj datę
    public void resetDate(){
        currentDate = LocalDate.now().withDayOfMonth(1);
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
                monthName = accessor.getMessage("lang.january",localeHolder.getLocale());
                break;
            case 2:
                monthName = accessor.getMessage("lang.february",localeHolder.getLocale());
                break;
            case 3:
                monthName = accessor.getMessage("lang.march",localeHolder.getLocale());
                break;
            case 4:
                monthName = accessor.getMessage("lang.april",localeHolder.getLocale());
                break;
            case 5:
                monthName = accessor.getMessage("lang.may",localeHolder.getLocale());
                break;
            case 6:
                monthName = accessor.getMessage("lang.june",localeHolder.getLocale());
                break;
            case 7:
                monthName = accessor.getMessage("lang.july",localeHolder.getLocale());
                break;
            case 8:
                monthName = accessor.getMessage("lang.august",localeHolder.getLocale());
                break;
            case 9:
                monthName = accessor.getMessage("lang.september",localeHolder.getLocale());
                break;
            case 10:
                monthName = accessor.getMessage("lang.october",localeHolder.getLocale());
                break;
            case 11:
                monthName = accessor.getMessage("lang.november",localeHolder.getLocale());
                break;
            case 12:
                monthName = accessor.getMessage("lang.december",localeHolder.getLocale());
                break;
        }
        return monthName;
    }

    public void resetLocale(){
        monthName = monthName(month);
    }

}
