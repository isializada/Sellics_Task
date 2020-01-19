# Keyword score estimation

## Summary
It is imperative for every Amazon seller to know which keywords are hot and which ones are not, and specifically which ones are important for each one of their products. Application estimate score for the typed keyword to the search input and it helps to sellers for compare keywords. 
Service use **Amazon Autocomplete API** for the solution. The API gives top ten results for the typed keyword. 

## How it works?
If typed keyword exists in the top ten list, we calculate the score by keyword’s index and we do this operation for every typed input till the end of the keyword (e.g “l”, “li”, “lin”, “linu” and “linux”). During our process, we sum all inputs score and find average by keyword. At the end of the process, we will get a score for the keyword between 0 and 100.

## Unit tests are implemented.

## Deployment
After running the application, you can find keyword score by using this URL: 	

localhost:8088/estimate?keyword=**YourKeyword**
