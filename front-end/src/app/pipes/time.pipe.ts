import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'time'
})
export class TimePipe implements PipeTransform {

  transform(value: number): string {
    if (isNaN(value) || value < 0) {
      return `${value}` + ' ms';
    }

    const minutes = Math.floor((value % (60 * 60 * 1000)) / (60 * 1000));
    const seconds = Math.floor((value % (60 * 1000)) / 1000);

    return `${minutes}m ${seconds}s`;
  }

}
