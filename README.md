# PlayList-AndroidApp
It's the WUSTL CSE438' assignment. A playlist android app,  using the Last.fm API to query for music data. 
-------------------------------------------------------------------------------
Basic function: 
  The app displays the current top tracks in a GridView on startup The app uses a tab bar with two tabs, one for searching for tracks and one for looking at playlist Data is pulled from the API and processed into a GridView on the main page.
  A Fragment to display the results seamlessly. 
  If selecting a track from the GridView opens a new activity with the track cover, title, and 3 other pieces of information: playcount, duration, and play link as well as the ability to save it to the playlist. 
  User can change search query by editing text ﬁeld. User can save a track to their playlist, and the track is saved into a SQLite database. 
  User can delete a track from the playlist (deleting it from the SQLite database itself).  
  Greatly improve the UI, conluding hide ugly title tab bar, change the text size and color, change the button' shape and add a frame, change the background. Repair the bug that if search an unexisit name the app will shut down. 
  Can delete the music in playlist both by seach and click in details or in playlist. 
  Can play the music in playlist and it will jump to the last.fm 's music playing page. If you have spotify, you can choose to play by spotify.
Still can improve: 
  Though wrong search will not call shut down, but I will add a alert dialog in the future. 
  Duplicate add to play list are allowed. It' hard to say we should do so all not.
