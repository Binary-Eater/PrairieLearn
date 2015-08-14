
# Writing tests

Each test is a single directory in the `tests` folder. The name of the directory is the `tid` (test ID). The directory must contain a single file called `info.json` that describes the test.

* [Format specification for test `info.json`](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/backendConfig.json)

The `options` field inside the test `info.json` has a format that depends on the test type (see the next section).

## Test types

Each test has a `type`, as listed below. A randomized test is one where each student gets a different set of questions in a randomized order, while a non-randomized test shows all students the same list of questions in the same order. Broadly speaking, randomized tests are designed for exams and non-randomized tests are designed for homeworks.

Type        | Randomized | Options format                                                                                                          | Description
---         | ---        | ---                                                                                                                     | ---
`Basic`     | No         | [Basic options](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/BasicTestOptions.json)         | A test scored by averaging all attempt scores for each question, then averaging the question scores.
`Adaptive`  | No         | [Adaptive options](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/AdaptiveTestOptions.json)   | An adaptively-scored test that gives and subtracts points based on a mastery estimate for the student.
`Game`      | No         | [Game options](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/GameTestOptions.json)           | A gamified test that rewards repeated correct answers to questions.
`Exam`      | Yes        | [Exam options](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/ExamTestOptions.json)           | A simple exam, where the score is the fraction of correct question answers.
`PracExam`  | Yes        | [PracExam options](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/PracExamTestOptions.json)   | Like `Exam`, but students can generate their own repeated random test instances for practice.
`RetryExam` | Yes        | [RetryExam options](https://github.com/PrairieLearn/PrairieLearn/blob/master/backend/schemas/RetryExamTestOptions.json) | An exam where students can grade their answers at any time, and retry question for reduced points.

## Question randomization algorithm

## Server modes

Each user accesses the PrairieLearn server in a `mode`, as listed below. This can be used to restrict access to tests based on the current mode.

Mode     | When active
---      | ---
`Exam`   | When the user is on a computer in the Computer Based Testing Faciltiy labs (determined by IP range), or when the user has overridden the mode to be `Exam` (only possible for `Instructor`).
`Public` | In all other cases.

## Access control

By default, a test is accessible in `Public` mode to all users at any time, and is only available to `Instructor` users in `Exam` mode. To change these defaults, the `allowAccess` option can be used in the test's `info.json` file. As an example:

    "allowAccess": [
        {
            "mode": "Public",
            "role": "TA"
        },
        {
            "mode": "Exam",
            "startDate": "2014-07-07T00:00:01",
            "endDate": "2014-07-10T23:59:59"
        }
    ],

The above `allowAccess` directive means that this test is available under two different circumstances. First, users who are at least a `TA` can access the test in `Public` mode at any time. Second, any user can access this test in `Exam` mode from July 7th to July 10th.

The general format of `allowAccess` is:

    "allowAccess": [
        { <accessRule1> },
        { <accessRule2> },
        { <accessRule3> }
    ],

Each `accessRule` is an object that specifies a set of circumstances under which the test is accessible. If any of the access rules gives access, then the test is accessible. Each access rule can have one or more restrictions:

Access restriction | Meaning
---                | ---
`mode`             | Only allow access from this server mode.
`role`             | Require at least this role to access.
`startDate`        | Only allow access after this date.
`endDate`          | Only access access before this date.

Each access role will only grant access if all of the restrictions are satisfied.

In summary, `allowAccess` uses the algorithm:

    each accessRule is True if (restriction1 AND restriction2 AND restriction3)
    allowAccess is True if (accessRule1 OR accessRule2 OR accessRule3)