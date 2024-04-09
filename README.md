Collaborator Guide for Using Git in Android Studio:

Setting Up the Project:
1) Downloading the Project
2) Clone the “Collectors Corner” repository from GitHub to your local machine.
3) Open Android Studio and select File > New > Project from Version Control.
4) Enter the URL of the repository and choose the directory where you want to store the project files.
Click Clone.

Configuring Version Control System (VCS):
1) Once the project is opened in Android Studio, go to VCS > Enable Version Control Integration.
2) Select Git from the list and click OK.

Managing Remotes:
1) Go to VCS > Git > Remotes.
2) Verify that the remote named origin is pointing to the “Collectors Corner” repository.
3) If not, add the correct remote repository URL.

Fetching the Latest Changes:
1) Before starting work, always fetch the latest changes from the remote repository.
2) Go to VCS > Git > Fetch.

Setting the Correct Branch:
1) Ensure you are on the main branch, as this is the primary branch for the project.
2) If you are on a different branch, switch to main by going to VCS > Git > Branches and selecting main.

Making Changes and Committing:
Making Changes:
1) Work on the project files as needed.
2) Save your changes.

Committing Locally:
1) Go to VCS > Commit (or press Ctrl + K).
2) Select the files you want to commit.
3) Write a clear and concise commit message.
4) Click Commit.

Pushing Changes to the Repository:
Pushing to Remote
1) After committing your changes, push them to the remote repository.
2) Go to VCS > Git > Push (or press Ctrl + Shift + K).
3) Confirm the branch you are pushing to (should be main).
4) Click Push.

Syncing with the Team:
Regular Updates
1) Communicate regularly with your team about the changes you are making.
2) Pull the latest changes from main before starting new work to avoid conflicts.
3) Resolve any merge conflicts that arise promptly and carefully.

Best Practices:
Always pull before starting work.
- Commit frequently with meaningful messages.
- Push regularly to keep the repository updated.
- Communicate with your team to stay in sync.

PLEASE NOTE:
If you guys experience branch conflicts or branch issues and you think you should commit and push, please DO NOT DO SO, because whatever is being updated on the push
it can have a negative impact on the main repo and then we have to use our copies to reupload the copied version and then we can work again but it is important to KEEP
COPIES OF THE PROJECT FILE WE ARE WORKING ON OUR LOCAL MACHINES OR VM's.


Regular updates and notifications will be made available here, thanks. 
