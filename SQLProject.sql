-- Drop existing Tables in order to start from clean state
DROP TABLE IF EXISTS Customer CASCADE;
DROP TABLE IF EXISTS Project CASCADE;
DROP TABLE IF EXISTS Developer CASCADE;
DROP TABLE IF EXISTS Skill CASCADE;
DROP TABLE IF EXISTS Milestone CASCADE;
DROP TABLE IF EXISTS Task CASCADE;
DROP TABLE IF EXISTS BugfixTask CASCADE;
DROP TABLE IF EXISTS FeatureTask CASCADE;
DROP TABLE IF EXISTS CodeReviewTask CASCADE;
DROP TABLE IF EXISTS JuniorDeveloper CASCADE;
DROP TABLE IF EXISTS MidDeveloper CASCADE;
DROP TABLE IF EXISTS SeniorDeveloper CASCADE;
DROP TABLE IF EXISTS LeadDeveloper CASCADE;
DROP TABLE IF EXISTS HasSkill CASCADE;
DROP TABLE IF EXISTS DueDateChange CASCADE;
DROP TABLE IF EXISTS Estimates CASCADE;
DROP TABLE IF EXISTS Orders CASCADE;
DROP TABLE IF EXISTS Reviews CASCADE;
DROP TABLE IF EXISTS TechLeads CASCADE;
DROP TABLE IF EXISTS TimeLog CASCADE;



-- Create Tables in "blank" Database
CREATE TABLE Customer (
	vatRegNo VARCHAR(15),
    name VARCHAR(50) NOT NULL,
    address VARCHAR(250) NOT NULL,
    PRIMARY KEY (vatRegNo)
);

CREATE TABLE Project(
	code VARCHAR(20),
    description VARCHAR(50) NOT NULL,
    startingDate DATE NOT NULL,
    endingDate DATE NOT NULL,
    status VARCHAR(10) NOT NULL,
    PRIMARY KEY (code)
);

CREATE TABLE Developer(
	employeeId INT,
    name VARCHAR(50) NOT NULL,
    hiringDate DATE NOT NULL,
    email VARCHAR(50) NOT NULL,
    weeklyCapHrs INT NOT NULL,
    PRIMARY KEY (employeeId)
);

CREATE TABLE Skill(
	name VARCHAR(50),
    PRIMARY KEY (name)
);

CREATE TABLE Milestone(
	project VARCHAR(20),
    code VARCHAR(20),
    startingDate DATE NOT NULL,
    endingDate DATE NOT NULL,
    critical BOOLEAN NOT NULL,
    PRIMARY KEY (project,code),
    FOREIGN KEY (project) REFERENCES Project(code)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE Task(
	taskId INT,
    description VARCHAR(50) NOT NULL,
    status VARCHAR(15) NOT NULL,
    dueDate DATE NOT NULL,
    dateReleased DATE,
    creationDate DATE NOT NULL,
    completionDate DATE,
    project VARCHAR(20) NOT NULL,
    milestone VARCHAR(20) NOT NULL,
    assignedDeveloper INT,
    PRIMARY KEY (taskId),
    FOREIGN KEY (project, milestone) REFERENCES Milestone(project, code)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (assignedDeveloper) REFERENCES Developer(employeeId)
        ON DELETE SET NULL
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE BugfixTask(
	taskId INT,
    impact VARCHAR(6) NOT NULL,
    PRIMARY KEY (taskId),
    FOREIGN KEY (taskId) REFERENCES Task(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE FeatureTask(
	taskId INT,
    complexity VARCHAR(6) NOT NULL,
    PRIMARY KEY (taskId),
    FOREIGN KEY (taskId) REFERENCES Task(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE CodeReviewTask(
	taskId INT,
    PRIMARY KEY (taskId),
    FOREIGN KEY (taskId) REFERENCES Task(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE JuniorDeveloper(
	employeeId INT,
    PRIMARY KEY (employeeId),
    FOREIGN KEY (employeeId) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE MidDeveloper(
	employeeId INT,
    PRIMARY KEY (employeeId),
    FOREIGN KEY (employeeId) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE SeniorDeveloper(
	employeeId INT,
    PRIMARY KEY (employeeId),
    FOREIGN KEY (employeeId) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE LeadDeveloper(
	employeeId INT,
    PRIMARY KEY (employeeId),
    FOREIGN KEY (employeeId) REFERENCES SeniorDeveloper(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE TimeLog(
	developer INT,
    task INT,
    startDate DATE,
    startTime TIME,
    endingDate DATE NOT NULL,
    endingTime TIME NOT NULL,
    timeWorkedHrs DECIMAL NOT NULL,
    PRIMARY KEY (developer, task, startDate, startTime),
    FOREIGN KEY (developer) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (task) REFERENCES Task(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE DueDateChange(
	developer INT,
    task INT,
    dateChanged DATE,
    oldDueDate DATE,
    newDueDate DATE,
    reason VARCHAR(100),
    timeWorkedUntilNowHrs DECIMAL,
    currentEstimateHrs DECIMAL,
    PRIMARY KEY (developer, task, dateChanged, oldDueDate, newDueDate),
    FOREIGN KEY (developer) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (task) REFERENCES Task(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE Orders(
	project VARCHAR(20),
    customer VARCHAR(15),
    PRIMARY KEY (project, customer),
    FOREIGN KEY (project) REFERENCES Project(code)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (customer) REFERENCES Customer(vatRegNo)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE Estimates(
	task INT,
    developer INT NOT NULL,
    estimatedEffortHrs INT NOT NULL,
    estimationDate DATE NOT NULL,
    PRIMARY KEY (task),
    FOREIGN KEY (task) REFERENCES Task(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (developer) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE Reviews(
	developer INT,
    codeReviewTask INT,
    result VARCHAR(8) NOT NULL,
    PRIMARY KEY (developer, codeReviewTask),
    FOREIGN KEY (developer) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (codeReviewTask) REFERENCES CodeReviewTask(taskId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE HasSkill(
	developer INT,
    skill VARCHAR(50),
    PRIMARY KEY (developer, skill),
    FOREIGN KEY (developer) REFERENCES Developer(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (skill) REFERENCES Skill(name)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE Techleads(
	project VARCHAR(20),
    leadDeveloper INT NOT NULL,
    PRIMARY KEY (project),
    FOREIGN KEY (project) REFERENCES Project(code)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (leadDeveloper) REFERENCES LeadDeveloper(employeeId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        DEFERRABLE INITIALLY DEFERRED
);



-- >>> Developer Completeness Constraint >>>
CREATE OR REPLACE FUNCTION DeveloperCheckCompleteness()
RETURNS TRIGGER AS $$
BEGIN
	IF NOT (
		(NEW.employeeId IN (SELECT employeeId FROM JuniorDeveloper)) OR 
		(NEW.employeeId IN (SELECT employeeId FROM MidDeveloper)) OR 
		(NEW.employeeId IN (SELECT employeeId FROM SeniorDeveloper))
	) THEN
		RAISE EXCEPTION 'Developer completeness constraint is not fulfilled for employeeId %.', NEW.employeeId;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER DeveloperOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON Developer
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION DeveloperCheckCompleteness();
-- <<< Developer Completeness Constraint <<<



-- >>> Developer Disjointness Constraint >>>
CREATE OR REPLACE FUNCTION JuniorDeveloperCheckDisjointness()
RETURNS TRIGGER AS $$
BEGIN
	IF (
		(NEW.employeeId IN (SELECT employeeId FROM MidDeveloper)) OR
		(NEW.employeeId IN (SELECT employeeId FROM SeniorDeveloper))
	) THEN
		RAISE EXCEPTION 'Developer disjointness constraint is not fulfilled for employeeId %.', NEW.employeeId;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER JuniorDeveloperOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON JuniorDeveloper
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION JuniorDeveloperCheckDisjointness();

CREATE OR REPLACE FUNCTION MidDeveloperCheckDisjointness()
RETURNS TRIGGER AS $$
BEGIN
	IF (
		(NEW.employeeId IN (SELECT employeeId FROM JuniorDeveloper)) OR
		(NEW.employeeId IN (SELECT employeeId FROM SeniorDeveloper))
	) THEN
		RAISE EXCEPTION 'Developer disjointness constraint is not fulfilled for employeeId %.', NEW.employeeId;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER MidDeveloperOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON MidDeveloper
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION MidDeveloperCheckDisjointness();

CREATE OR REPLACE FUNCTION SeniorDeveloperCheckDisjointness()
RETURNS TRIGGER AS $$
BEGIN
	IF (
		(NEW.employeeId IN (SELECT employeeId FROM JuniorDeveloper)) OR
		(NEW.employeeId IN (SELECT employeeId FROM MidDeveloper))
	) THEN
	    RAISE EXCEPTION 'Developer disjointness constraint is not fulfilled for employeeId %.', NEW.employeeId;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER SeniorDeveloperOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON SeniorDeveloper
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION SeniorDeveloperCheckDisjointness();
-- <<< Developer Disjointness Constraint <<<



-- >>> Task Completeness Constraint >>>
CREATE OR REPLACE FUNCTION TaskCheckCompleteness()
RETURNS TRIGGER AS $$
BEGIN
	IF NOT (
		(NEW.taskId IN (SELECT taskId FROM BugfixTask)) OR 
		(NEW.taskId IN (SELECT taskId FROM FeatureTask)) OR 
		(NEW.taskId IN (SELECT taskId FROM CodeReviewTask)) 
	) THEN
		RAISE EXCEPTION 'Task completeness constraint is not fulfilled for taskId %.', NEW.taskId;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TaskOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON Task
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION TaskCheckCompleteness();
-- <<< Task Completeness Constraint <<<



-- >>> Task Disjointness Constraint >>>
CREATE OR REPLACE FUNCTION BugfixTaskCheckDisjointness()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (NEW.taskId IN (SELECT taskId FROM FeatureTask)) OR
        (NEW.taskId IN (SELECT taskId FROM CodeReviewTask))
    ) THEN
        RAISE EXCEPTION 'Task disjointness constraint is not fulfilled for taskId %.', NEW.taskId;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER BugfixTaskOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON BugfixTask
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION BugfixTaskCheckDisjointness();

CREATE OR REPLACE FUNCTION FeatureTaskCheckDisjointness()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (NEW.taskId IN (SELECT taskId FROM BugfixTask)) OR
        (NEW.taskId IN (SELECT taskId FROM CodeReviewTask))
    ) THEN
        RAISE EXCEPTION 'Task disjointness constraint is not fulfilled for taskId %.', NEW.taskId;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER FeatureTaskOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON FeatureTask
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION FeatureTaskCheckDisjointness();

CREATE OR REPLACE FUNCTION CodeReviewTaskCheckDisjointness()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (NEW.taskId IN (SELECT taskId FROM BugfixTask)) OR
        (NEW.taskId IN (SELECT taskId FROM FeatureTask))
    ) THEN
        RAISE EXCEPTION 'Task disjointness constraint is not fulfilled for taskId %.', NEW.taskId;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER CodeReviewTaskOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON CodeReviewTask
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION CodeReviewTaskCheckDisjointness();
-- <<< Task Disjointness Constraint <<<

-- >>> (Some) External Constraints >>>
CREATE OR REPLACE FUNCTION CheckDates()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.startingDate > NEW.endingDate THEN
        RAISE EXCEPTION 'Starting date must be less than or equal to ending date.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER ProjectOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON Project
FOR EACH ROW
EXECUTE FUNCTION CheckDates();

CREATE CONSTRAINT TRIGGER MilestoneOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON Milestone
FOR EACH ROW
EXECUTE FUNCTION CheckDates();


CREATE OR REPLACE FUNCTION MilestoneCheckProjectDates()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Project
        WHERE Project.code = NEW.project
            AND Project.startingDate <= NEW.startingDate
            AND Project.endingDate >= NEW.endingDate
    ) THEN
        RAISE EXCEPTION 'No corresponding project with overlapping dates found for milestone % %.', NEW.project, NEW.code;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER MilestoneOnAfterInsertOrUpdateTrigger2
AFTER INSERT OR UPDATE
ON Milestone
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION MilestoneCheckProjectDates();


CREATE OR REPLACE FUNCTION TimeLogCheckAssignedDeveloper()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Task
        WHERE Task.taskId = NEW.task
            AND Task.assignedDeveloper = NEW.developer
    ) THEN
        RAISE EXCEPTION 'Task % is not assigned to developer %.', NEW.task, NEW.developer;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TimeLogOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON TimeLog
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION TimeLogCheckAssignedDeveloper();


CREATE OR REPLACE FUNCTION TimeLogCheckDates()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (NEW.endingDate < NEW.startDate) OR
        ((NEW.endingDate = NEW.startDate) AND (NEW.endingTime < NEW.startTime))
    ) THEN
        RAISE EXCEPTION 'End Date/Time (% %) lies before Start Date/Time (% %) in Time Log % %', NEW.endingDate, NEW.endingTime, NEW.startingDate, NEW.startingTime, NEW.task, NEW.developer;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TimeLogOnAfterInsertOrUpdateTrigger2
AFTER INSERT OR UPDATE
ON TimeLog
FOR EACH ROW
EXECUTE FUNCTION TimeLogCheckDates();


CREATE OR REPLACE FUNCTION CheckBugfixTaskAssignedDeveloper()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (NEW.taskId IN (SELECT taskId FROM BugfixTask WHERE impact = 'high')) AND
        (NEW.assignedDeveloper IN (SELECT employeeId FROM JuniorDeveloper))
    ) THEN
        RAISE EXCEPTION 'Bugfix Task % must be assigned to a Mid or Senior Developer', NEW.taskId;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TaskOnAfterInsertOrUpdateTrigger2
AFTER INSERT OR UPDATE
ON Task
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION CheckBugfixTaskAssignedDeveloper();


CREATE OR REPLACE FUNCTION CheckFeatureTaskAssignedDeveloper()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (NEW.taskId IN (SELECT taskId FROM FeatureTask WHERE complexity = 'high')) AND
        (
            (NEW.assignedDeveloper IN (SELECT employeeId FROM JuniorDeveloper)) OR
            (NEW.assignedDeveloper IN (SELECT employeeId FROM MidDeveloper))
        )
    ) THEN
        RAISE EXCEPTION 'Feature Task % must be assigned to a Senior Developer', NEW.taskId;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TaskOnAfterInsertOrUpdateTrigger3
AFTER INSERT OR UPDATE
ON Task
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION CheckFeatureTaskAssignedDeveloper();


CREATE OR REPLACE FUNCTION CheckAtMostThreeWorkableTasks()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (SELECT COUNT(taskId) FROM Task WHERE (assignedDeveloper = NEW.assignedDeveloper) AND (status in ('authorized','in progress'))) > 3
    ) THEN
        RAISE EXCEPTION 'Developer % already has 3 workable Tasks assigned.', NEW.assignedDeveloper;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TaskOnAfterInsertOrUpdateTrigger4
AFTER INSERT OR UPDATE
ON Task
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION CheckAtMostThreeWorkableTasks();


CREATE OR REPLACE FUNCTION CheckTimeLogWithinCompletionDate()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        (SELECT completionDate FROM Task WHERE taskId = NEW.task) < NEW.endingDate
    ) THEN
        RAISE EXCEPTION 'Ending Date % lies after Completion Date of Task %', NEW.endingDate, NEW.taskId;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER TimeLogOnAfterInsertOrUpdateTrigger3
AFTER INSERT OR UPDATE
ON TimeLog
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION CheckTimeLogWithinCompletionDate();


CREATE OR REPLACE FUNCTION CheckTimeLogForReviewExists()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM TimeLog
        WHERE task = NEW.codeReviewTask
            AND developer = NEW.developer
    ) THEN
        RAISE EXCEPTION 'Review of Task % by Developer % does not have a connected Time Log entry.', NEW.codeReviewTask, NEW.developer;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER CodeReviewTaskOnAfterInsertOrUpdateTrigger
AFTER INSERT OR UPDATE
ON Reviews
FOR EACH ROW
EXECUTE FUNCTION CheckTimeLogForReviewExists();
-- <<< External Constraints <<<



--- ###### SAMPLE DATA ######
BEGIN TRANSACTION;
    INSERT INTO Customer(vatRegNo, name, address) VALUES
        ('IT00187320213', 'A. Loacker Spa/AG', 'Via Gasterer Weg 3, I-39054 Auna di Sotto/Unterinn (BZ), Italia'),
        ('IT02238100214', 'EOS AG', 'G. Di Vittorio Strasse 23, 39100 Bozen, Italien');

    INSERT INTO Project(code, description, startingDate, endingDate, status) VALUES
        ('LCKIT_P1', 'Developer Time Sheet App', '2022-05-01', '2022-12-31', 'complete'),
        ('EOS_P2', 'Consultant Interactive Tutorial', '2025-01-01', '2025-12-31', 'active');

    INSERT INTO Developer(employeeId, name, hiringDate, email, weeklyCapHrs) VALUES
        (1, 'Wolfram Eschenbach', '2018-01-15', 'wolfram.eschenbach@example.com', 40),
        (2, 'Arthas Menethil', '2019-03-20', 'arthas.menethil@example.com', 32),
        (3, 'Winnie Pooh', '2020-06-10', 'winnie.pooh@example.com', 24),
        (4, 'Christopher Robin', '2021-09-05', 'christopher.robin@example.com', 40),
        (5, 'Geralt Of Rivia', '2022-01-10', 'geralt.rivia@example.com', 40);

    INSERT INTO Skill(name) VALUES
        ('JavaScript'),
        ('Python'),
        ('SQL'),
        ('Data Analysis'),
        ('Resource Management');

    INSERT INTO Milestone(project, code, startingDate, endingDate, critical) VALUES
        ('LCKIT_P1', 'MVP', '2022-05-01', '2022-06-30', true),
        ('LCKIT_P1', 'PHASE2', '2022-07-01', '2022-10-31', false),
        ('EOS_P2', 'DESIGN', '2025-01-01', '2025-02-28', true),
        ('EOS_P2', 'DEVELOPMENT', '2025-03-01', '2025-07-31', true);

    INSERT INTO Task(taskId, description, status, dueDate, dateReleased, creationDate, completionDate, project, milestone, assignedDeveloper) VALUES
        (1, 'Develop initial MVP features', 'completed', '2022-06-15', '2022-05-01', '2022-05-01', '2022-06-15', 'LCKIT_P1', 'MVP', 1),
        (2, 'Conduct integration testing', 'completed', '2022-12-15', '2022-12-02', '2022-12-01', '2022-12-15', 'LCKIT_P1', 'PHASE2', 2),
        (3, 'Create design mockups', 'in progress', '2025-01-31', '2025-01-01', '2025-01-01', NULL, 'EOS_P2', 'DESIGN', 3),
        (4, 'Develop core functionalities', 'authorized', '2025-07-31', '2025-02-03', '2025-02-01', NULL, 'EOS_P2', 'DEVELOPMENT', NULL),
        (5, 'Fix critical bug in module A', 'in progress', '2025-03-31', '2025-02-01', '2025-02-01', NULL, 'EOS_P2', 'DEVELOPMENT', 1),
        (6, 'Resolve performance issue', 'in progress', '2025-03-31', '2025-02-05', '2025-02-05', NULL, 'EOS_P2', 'DEVELOPMENT', 4),
        (7, 'Code review for core feature 1', 'completed', '2025-01-15', '2025-01-01', '2025-01-01', '2025-01-14', 'EOS_P2', 'DEVELOPMENT', 1),
        (8, 'Code review for core feature 2', 'completed', '2025-01-31', '2025-01-10', '2025-01-10', '2025-01-24', 'EOS_P2', 'DEVELOPMENT', 4),
        (9, 'New feature implementation', 'not authorized', '2025-07-31', NULL, '2025-01-15', NULL, 'EOS_P2', 'DEVELOPMENT', NULL);

    INSERT INTO BugfixTask(taskId, impact) VALUES
        (5, 'high'),
        (6, 'low');

    INSERT INTO FeatureTask(taskId, complexity) VALUES
        (1, 'high'),
        (2, 'medium'),
        (3, 'medium'),
        (4, 'high'),
        (9, 'low');

    INSERT INTO CodeReviewTask(taskId) VALUES
        (7),
        (8);

    INSERT INTO JuniorDeveloper(employeeId) VALUES
        (2),
        (3);

    INSERT INTO MidDeveloper(employeeId) VALUES
        (4);

    INSERT INTO SeniorDeveloper(employeeId) VALUES
        (1),
        (5);

    INSERT INTO LeadDeveloper(employeeId) VALUES
        (1);

    INSERT INTO Orders(project, customer) VALUES
        ('LCKIT_P1', 'IT00187320213'),
        ('EOS_P2', 'IT02238100214');

    INSERT INTO Estimates(task, developer, estimatedEffortHrs, estimationDate) VALUES
        (1, 1, 80, '2022-05-01'),
        (2, 1, 40, '2022-12-01'),
        (3, 1, 60, '2025-01-01'),
        (4, 1, 120, '2025-03-01');

    INSERT INTO Reviews(developer, codeReviewTask, result) VALUES
        (1, 7, 'success'),
        (4, 8, 'rejected');

    INSERT INTO HasSkill(developer, skill) VALUES
        (1, 'JavaScript'),
        (1, 'Python'),
        (1, 'Data Analysis'),
        (1, 'Resource Management'),
        (2, 'SQL'),
        (3, 'Data Analysis'),
        (4, 'Python'),
        (4, 'JavaScript'),
        (5, 'JavaScript'),
        (5, 'Data Analysis');

    INSERT INTO TechLeads(project, leadDeveloper) VALUES
        ('LCKIT_P1', 1),
        ('EOS_P2', 1);

    INSERT INTO TimeLog(developer, task, startDate, startTime, endingDate, endingTime, timeWorkedHrs) VALUES
        (1, 1, '2022-05-01', '08:30:00', '2022-05-01', '16:30:00', 8.0),
        (2, 2, '2022-12-01', '09:30:00', '2022-12-01', '17:30:00', 8.0),
        (3, 3, '2025-01-01', '08:00:00', '2025-01-01', '12:00:00', 4.0);

    INSERT INTO DueDateChange(developer, task, dateChanged, oldDueDate, newDueDate, reason, timeWorkedUntilNowHrs, currentEstimateHrs) VALUES
        (1, 1, '2022-05-15', '2022-06-10', '2022-06-15', 'Scope change', 40.0, 80.0),
        (2, 2, '2022-12-10', '2022-12-20', '2022-12-15', 'Resource availability', 20.0, 40.0),
        (3, 3, '2025-01-15', '2025-01-20', '2025-01-31', 'Design complexity', 10.0, 60.0),
        (1, 4, '2025-03-15', '2025-07-20', '2025-07-31', 'Additional features', 30.0, 120.0);
END TRANSACTION;
