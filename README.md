# Starter Upper

Undergraduate computer science students and faculty typically use Blackboard, Moodle, or email for work submission.
Although software developers typically submit work using git, few faculty adopt git in the classroom, depriving students of practical experience.
Getting started with git in the classroom can be tedious and error-prone when students misinterpret written instructions, which is why I wrote Starter Upper.
Starter Upper helps students set up accounts and repositories flawlessly on Git project hosting services with minimal time and overhead.
With any luck, faculty who use this program will move to using [Git on the cloud in the classroom](http://db.grinnell.edu/sigcse/sigcse2013/Program/viewAcceptedProposal.pdf?sessionType=paper&sessionNumber=257).

# What Starter Upper does

1. Configures SSH keys and Git, regardless of whether it's installed
2. (Optional) Sets up student Gravatars
3. Sets up project hosting accounts on services such as Bitbucket, GitHub or GitLab.
4. Clones the course (upstream) repository
5. Creates private repositories on project hosting services
6. Adds these remotes to the local git repository
7. Pushes to all remotes
8. Shares each remote with faculty

# What Starter Upper does not do

Starter Upper does not install software.
I recommend that faculty use package managers such as Chocolatey, Homebrew, apt-get, yum, or pacman for their respective OS to install Git and graphical frontends such as Git Extensions.