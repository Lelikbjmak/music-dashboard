import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatDate'
})
export class FormatDatePipe implements PipeTransform {

  transform(value: Date | string): unknown {
    const date = new Date(value);

    if (isNaN(date.getTime())) {
      return 'Invalid Date';
    }

    const formattedDate = new DatePipe('en-US').transform(date, 'yyyy-MM-dd');

    return formattedDate;
  }

}
