# AI Usage, Governance & Limitations

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## Mandatory Statutory Notice
All AI feature responses and UI screens must display:
> **"AI-assisted information only. Human officer confirmation is required. This output is not an approval, rejection, inspection result, or statutory decision."**

## Permitted Assistive Functions
1. **OCR Extraction Assistance**: Extracts candidate institution names, certificate numbers, and dates to pre-fill forms for human review.
2. **Document Classification**: Evaluates document headers to recommend likely checklist categories.
3. **Missing Document Checks**: Heuristically compares uploaded documents against requirements.
4. **Application Summaries**: Synthesizes open deficiencies and pending deadlines.
5. **Parameterized Natural Language Search**: Translates natural administrative queries into safe parameterized filters — raw SQL execution is prohibited.

## Prohibited Functions
- ❌ No automated approval or rejection of applications.
- ❌ No automated verification of document authenticity.
- ❌ No automated workflow stage transitions based on AI outputs.
- ❌ No execution of AI-generated raw SQL.

## Human Officer Sign-Off Requirement
Every AI suggestion requires an authorized human officer to review, provide optional comments, and sign off before any administrative action can proceed. All sign-offs are logged into the immutable SHA-256 audit ledger.
