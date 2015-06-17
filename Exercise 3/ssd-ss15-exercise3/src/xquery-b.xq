<stats>{
	for $sessions in  .//games/game
	return
	<session id="{data($sessions/@session)}"> {
		for $player in .//games/game[@session=data($sessions/@session)]/player
		return
		<correct player="{data($player/@ref)}">{
				let $asked := .//games/game[@session=data($sessions/@session)]/asked
				let $answers := .//categories/category/question[@id=data($asked/@question)]/answer[@correct='yes']

				for $givenanswer in .//games/game[@session=data($sessions/@session)]/asked/givenanswer[@player=data($player/@ref)]
				where $givenanswer/text() = $answers/text()
				return
					count($givenanswer)

		}</correct>
	}</session>

}</stats>
