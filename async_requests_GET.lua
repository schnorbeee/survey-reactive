-- async_requests_GET.lua
-- Aszinkron POST/GET kérések WebFlux, hibák message/detail loggal
-- POST elöl, GET után

local endpoints = {
--     { method = "POST", path = "/api/statuses", file = "./src/test/resources/testfiles/Statuses.csv" },
--     { method = "POST", path = "/api/members", file = "./src/test/resources/testfiles/Members.csv" },
--     { method = "POST", path = "/api/surveys", file = "./src/test/resources/testfiles/Surveys.csv" },
--     { method = "POST", path = "/api/participations", file = "./src/test/resources/testfiles/Participations.csv" },
    { method = "GET", path = "/api/members/by-survey-and-completed?surveyId=1" },
    { method = "GET", path = "/api/members/by-not-participated-survey-and-active?surveyId=1" },
    { method = "GET", path = "/api/surveys/by-member-id-and-completed?memberId=1" },
    { method = "GET", path = "/api/surveys/by-member-id-completion-points?memberId=1" },
    { method = "GET", path = "/api/surveys/all-statistic" }
}

function read_file(path)
    local file = io.open(path, "rb")
    if not file then return nil end
    local content = file:read("*all")
    file:close()
    return content
end

request = function()
    local e = endpoints[math.random(1, #endpoints)]
    local headers = {}
    local body = nil

    if e.method == "POST" then
        local file_content = read_file(e.file)
        if not file_content then return nil end
        -- WebFlux FilePart szimuláció multipart form-data
        body = "--boundary\r\nContent-Disposition: form-data; name=\"file\"; filename=\""..e.file.."\"\r\nContent-Type: text/csv\r\n\r\n"..file_content.."\r\n--boundary--\r\n"
        headers["Content-Type"] = "multipart/form-data; boundary=boundary"
    end

    return wrk.format(e.method, e.path, headers, body)
end

response = function(status, headers, body)
    if status >= 400 then
        local msg, detail = body:match('"message"%s*:%s*"([^"]*)".-"detail"%s*:%s*"([^"]*)"')
        print("HTTP "..status.." Error: message="..(msg or "N/A")..", detail="..(detail or "N/A"))
    end
end
