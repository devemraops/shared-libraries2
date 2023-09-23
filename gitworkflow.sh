# gitworkflow () {
#   location commit_message = "$1"
#   location branch = "$2"

#   # Set if commit message is none provided
#   if [ -z "${commit_message}" ]; then
#   commit_message = "working on stages"
#   fi
#   if [ -z "${branch}" ]; then
#   echo "No branch name was specified, using default: master";
#   branch="master"
#   fi

#   git add .
#   git commit -m "$commit_message"
#   git push origin "$branch"
# }

# Second way of commiting

gitworkflow() {
    local commit_message="working on the lint stage"
    local branch="master"

    # Process flags
    while [ "$#" -gt 0 ]; do
        case "$1" in
            -m|--message)
                commit_message="$2"
                shift 2
                ;;
            -b|--branch)
                branch="$2"
                shift 2
                ;;
            *)
                echo "Unknown parameter: $1"
                return 1
                ;;
        esac
    done

    git add .
    git commit -m "$commit_message"
    git push origin "$branch"
}