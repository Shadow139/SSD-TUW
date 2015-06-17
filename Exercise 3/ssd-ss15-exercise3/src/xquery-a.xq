<correct> {
	for $asked in .//games/game[@session='abcd']/asked
	
	for $answers in .//categories/category/question[@id=data($asked/@question)]/answer[@correct='yes']
	
	for $givenanswer in .//games/game[@session='abcd']/asked/givenanswer[@player='Bart']
	where $givenanswer/text() = $answers/text() 
	return 
	      $givenanswer
	           
} </correct> 